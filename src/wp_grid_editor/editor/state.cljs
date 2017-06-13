(ns wp-grid-editor.editor.state
  (:require-macros
   [javelin.core :as j])
  (:require
   [javelin.core :as j]
   [clojure.string :as s]
   [wp-grid-editor.ajax :as ajax]))

(j/defc main [])

(j/cell= (.log js/console (clj->js main)))

;; Convert text content to clojure and back
(def sc-regx #"\[([\w\_\-]+)\ ?([\w\_\-\=\"\.\ ]*)(\]|/\])([\w\W\n]*)")

(defn sc-end-regx [n]
  (re-pattern
   (str "(?:(\\[" n "\\ ?[\\w\\=\\\"\\-\\_]*\\])|(\\[\\/" n "\\]))([\\w\\W\\n]*)")))

(defn nestr [s] (when (-> s s/blank? not) s))

(defn shortcode-params [ps]
  (reduce
   #(let [[k v] (s/split %2 #"=")]
      (assoc %1 (keyword k) (s/replace v "\"" "")))
   {} (s/split ps #" ")))

(defn front-content [c fm]
  (let [cl (.-length c)
        fml (.-length fm)]
    (if (= cl fml)
      nil
      (nestr (subs c 0 (- cl fml))))))

(defn shortcode-contents [n c]
  (if (s/blank? n)
    [nil c]
    (let [regx (sc-end-regx n)]
      (loop [r "" count 0 restc c]
        (let [rer (re-find (sc-end-regx n) restc)
              [fm osc csc bc] rer]
          (if (s/blank? fm)
            [r restc]
            (let [fr (front-content restc fm)
                  r' (str r fr osc)]
              (if (-> osc s/blank? not)
                (recur r' (inc count) bc)
                (if (-> csc s/blank? not)
                  (if (zero? count)
                    [r' bc]
                    (recur (str r' csc) (dec count) bc)))))))))))

(defn make-content-node [c] {:element :content :content c})

(defn make-sc-node [sn sp sc selfc]
  (let [n {:key sn :element :shortcode
           :self-closing selfc
           :parameters (shortcode-params sp)}]
    (if selfc n (assoc n :nodes sc))))

(defn map-shortcodes [c]
  (when (-> c s/blank? not)
    (loop [r [] restc c]
      (let [[fm sn sp endt sr] (re-find sc-regx restc)]
        (if (s/blank? fm)
          (if (s/blank? restc) r (conj r (make-content-node restc)))
          (if-let [fr (front-content restc fm)]
            (let [r' (conj r (make-content-node fr))] (recur r' fm))
            (let [[sc restsc] (if (= endt "]")
                                (shortcode-contents sn sr)
                                [nil sr])
                  mksc! (partial make-sc-node sn sp)
                  r' (conj r (condp = endt
                               "]"  (mksc! (map-shortcodes sc) false)
                               "/]" (mksc! nil true)))]
              (if (s/blank? restsc)
                r'
                (recur r' restsc)))))))))

(defn shortcode-keys [sn]
  (loop [r [] n sn]
    (let [{:keys [key element self-closing nodes]} (first n)
          nn (rest n)]
      (if (= element :shortcode)
        (let [r' (conj r key)
              r'' (if (or self-closing (empty? nodes))
                    r' (into r' (shortcode-keys nodes)))]
          (if (empty? nn) r'' (recur r'' nn)))
        (if (empty? nn) r (recur r nn))))))

(j/defc= shortcodes (shortcode-keys main))
(j/defc= ushortcodes (distinct shortcodes))

(j/defc display-mods [])
(j/defc= display-mods-map (reduce #(assoc %1 (-> %2 :key) %2) {} display-mods))

;; Keeping grid composer in sync with default WP editor

(defn wp-active-editor [] (.-wpActiveEditor js/window))
(defn get-tmce-editor [] (.get js/tinyMCE (wp-active-editor)))
(defn get-ptext-editor [] (js/jQuery (str "#" (wp-active-editor))))

(defn get-wp-editor-content []
  (if-let [t (get-tmce-editor)]
    (.getContent t)
    (.val (get-ptext-editor))))

(defn set-wp-editor-content [c]
  (if-let [t (get-tmce-editor)]
    (.setContent t c)
    (.val (get-ptext-editor) c)))

(defn load-display-modules []
  (ajax/post-cell "getModulesDisplayIn"
             {:keys @ushortcodes} display-mods))

(defn enable! []
  (let [perf (.now js/performance)
        v (map-shortcodes (get-wp-editor-content))]
    (.log js/console (str "Whole conversion: " (- (.now js/performance) perf)))
    (reset! main v))
  (load-display-modules))

(defn disable! [] #_(set-wp-editor-content @main))
