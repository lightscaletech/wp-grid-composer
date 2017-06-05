(ns wp-grid-editor.editor.state
  (:require-macros
   [javelin.core :as j])
  (:require
   [javelin.core :as j]
   [clojure.string :as s]))

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

(defn make-content-node [c] {:type :content :content c})

(defn make-sc-node [sn sp sc selfc]
  (let [n {:name sn :type :shortcode
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

(j/defc= cmain (map-shortcodes main))

(j/cell= (.log js/console (clj->js cmain)))

;; Keeping grid composer in sync with default WP editor

(defn wp-active-editor [] (.-wpActiveEditor js/window))
(defn get-tmce-editor [] (.get js/tinyMCE (wp-active-editor)))
(defn get-ptext-editor [] (js/jQuery (str "#" (wp-active-editor))))

(defn get-wp-editor-content []
  (if-let [t (get-tmce-editor)]
    (do
      (.log js/console "Test getting rtext")
      (.getContent t))
    (do
      (.log js/console "Test getting ptext")
      (.val (get-ptext-editor)))))

(defn set-wp-editor-content [c]
  (if-let [t (get-tmce-editor)]
    (do
      (.log js/console "Test setting rtext")
      (.setContent t c))
    (do
      (.log js/console "Test setting ptext")
      (.val (get-ptext-editor) c))))

(defn enable! [] (reset! main (get-wp-editor-content)))
(defn disable! [] (set-wp-editor-content @main))
