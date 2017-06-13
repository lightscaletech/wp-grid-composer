(ns wp-grid-editor.core
  (:require-macros
   [hoplon.core :as h]
   [javelin.core :as j])
  (:require
   [hoplon.jquery]
   [hoplon.core :as h]
   [javelin.core :as j]
   [wp-grid-editor.editor :as e]
   [wp-grid-editor.editor.state :as s]))

(j/defc wordpress-editor true)

(def wordpress-editor-id "postdivrich")

(def wpe #(js/jQuery (str "#" wordpress-editor-id)))

(defn show-gc []
  (.css (wpe) (clj->js {:visibility "collapse" :height "0"}))
  (s/enable!))

(defn show-wp []
  (.css (wpe) (clj->js {:visibility "visible" :height "initial"}))
  (s/disable!))

(defn change-editor []
  (if @wordpress-editor (show-gc) (show-wp))
  (swap! wordpress-editor not))

(h/defelem switch-editor []
  (h/button
   :class "button" :type "button"
   :click #(change-editor)
   (h/if-tpl (j/cell= wordpress-editor)
             "Grid Composer"
             "Wordpress Editor")))

(defn editor-ready [cb]
  (js/jQuery #(cb)))

(h/defelem main []
  (editor-ready
   #(let [enabled (pos? (.-_lsgc_editor_enabled__ js/window))]
      (if enabled
        (do
          (reset! wordpress-editor false)
          (show-gc))
        (do
          (reset! wordpress-editor true)
          (show-wp)))))
  (h/div
   (h/br)
   (switch-editor)
   (h/when-tpl (j/cell= (false? wordpress-editor))
     (e/editor))))

(-> js/document (.getElementById "lsgc_editor_container") (.appendChild (main)))
