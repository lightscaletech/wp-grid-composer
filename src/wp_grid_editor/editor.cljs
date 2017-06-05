(ns wp-grid-editor.editor
  (:require-macros
   [hoplon.core :as h]
   [javelin.core :as j])
  (:require
   [hoplon.core :as h]
   [javelin.core :as j]
   [wp-grid-editor.editor.state :as s]))

(h/defelem menu-bar-main []
  (h/div
   :class "lsgc_main_bar"
   ))

(h/defelem menu [_ items]
  (h/div
   :class "lsgc_menu"
   items))

(h/defelem self-closing-shortcode [{:keys [elem]}]
  (j/cell-let
   [{:keys [parameters] n :name} elem]
   [(menu)
    (h/h3 n)
    (h/ul
     (h/loop-tpl
      :bindings [[k v] (j/cell= (vec parameters))]
      (h/li (j/cell= (str (name k) ": " v)))))]))

(h/defelem shortcode [{:keys [content elem]}]
  (j/cell-let
   [{:keys [name]} elem]
   [(menu)
    (h/h3 name)
    (content)]))

(h/defelem content [{{:keys [content]} :elem}]
  [(menu)
   (h/div content)])

(h/defelem render [{:keys [elems]}]
  (h/div
   (h/loop-tpl
    :bindings [{:keys [type self-closing nodes] :as elem} elems]
    (h/div
     (h/cond-tpl
      (j/cell= (and (= type :shortcode) self-closing))
      (self-closing-shortcode :elem elem)
      (j/cell= (and (= type :shortcode) (not self-closing)))
      (shortcode :elem elem :content (render :elems nodes))
      (j/cell= (= type :content))
      (content :elem elem))))))

(h/defelem editor []
  (h/div
   :class "lsgc_editor_wrap"
   (render :elems s/cmain)))
