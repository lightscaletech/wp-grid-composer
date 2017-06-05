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

(defn change-editor []
  (let [wpe(js/jQuery (str "#" wordpress-editor-id))]
    (if @wordpress-editor
      (do (.css wpe (clj->js {:visibility "collapse"}))
          (s/enable!))
      (do (.css wpe (clj->js {:visibility "visible"}))
          (s/disable!))))
  (swap! wordpress-editor not))

(h/defelem switch-editor []
  (h/button
   :class "button" :type "button"
   :click #(change-editor)
   (h/if-tpl (j/cell= wordpress-editor)
             "Grid Composer"
             "Wordpress Editor")))

(h/defelem main []
  (h/div
   (h/br)
   (switch-editor)
   (h/when-tpl (j/cell= (false? wordpress-editor))
     (e/editor))))

(-> js/document (.getElementById "lsgc_editor_container") (.appendChild (main)))
