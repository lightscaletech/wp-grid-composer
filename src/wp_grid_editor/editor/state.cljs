(ns wp-grid-editor.editor.state
  (:require-macros
   [javelin.core :as j])
  (:require
   [javelin.core :as j]
   [clojure.string :as s]
   [wp-grid-editor.ajax :as ajax]))

(j/defc main "")

;; Convert text content to clojure and back
(def sc-regx #"\[([\w\_\-]+)\ ?((?:[\w\_\-\=\"]+\ ?)*)(/\]|\])(.*)")

(defn sc-end-regx [n]
  (re-pattern
   (str "(?:(\\[" n "\\ ?[\\w\\=\\\"\\-\\_]*\\])|(\\[\\/" n "\\]))(.*)")))

(defn nestr [s] (when (-> s empty? not) s))

(defn shortcode-params [ps]
  (reduce
   #(let [[k v] (s/split %2 #"=")]
      (assoc %1 (keyword k) (s/replace v "\"" "")))
   {} (s/split ps #" ")))

(defn front-content [c fm] (nestr (subs c 0 (- (.-length c) (.-length fm)))))

(defn shortcode-contents [n c]
  (if (empty? n)
    [nil nil]
    (let [regx (sc-end-regx n)]
      (loop [r "" count 0 restc c]
        (let [rer (re-find (sc-end-regx n) restc)
              [fm osc csc bc] rer
              fr (front-content restc fm)
              r' (str r fr osc)]
          (if (-> osc empty? not)
            (recur r' (inc count) bc)
            (if (-> csc empty? not)
              (if (zero? count)
                [r' bc]
                (recur (str r' csc) (dec count) bc)))))))))

(defn make-content-node [c] {:element :content :content c})

(defn make-sc-node [sn sp sc selfc]
  (let [n {:key sn :element :shortcode
           :self-closing selfc
           :parameters (shortcode-params sp)}]
    (if selfc n (assoc n :nodes sc))))

(defn map-shortcodes [c]
  (when (-> c empty? not)
    (loop [r [] restc c]
      (let [[fm sn sp endt sr] (re-find sc-regx restc)]
        (if (empty? fm)
          (if (empty? restc) r (conj r (make-content-node restc)))
          (if-let [fr (front-content restc fm)]
            (recur (conj r (make-content-node fr)) fm)
            (let [[sc restsc] (if (= endt "]") (shortcode-contents sn sr))
                  mksc! (partial make-sc-node sn sp)
                  r' (conj r (condp = endt
                               "]"  (mksc! (map-shortcodes sc) false)
                               "/]" (mksc! nil true)))]
              (if (empty? restsc)
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
  (reset! main (map-shortcodes (get-wp-editor-content)))
  (load-display-modules))

(defn disable! [] #_(set-wp-editor-content @main))
