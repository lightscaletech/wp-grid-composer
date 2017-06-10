(ns wp-grid-editor.editor
  (:require-macros
   [hoplon.core :as h]
   [javelin.core :as j])
  (:require
   [hoplon.core :as h]
   [javelin.core :as j]
   [clojure.string :as string]
   [wp-grid-editor.editor.state :as s]
   [wp-grid-editor.editor.menus :as menus]))

(h/defelem self-closing-shortcode [{:keys [elem]}]
  (j/cell-let
   [{:keys [parameters key] n :name} elem]
   [(menus/module)
    (h/h3 (j/cell= (or n key)))
    (h/ul
     (h/loop-tpl
      :bindings [[k v] (j/cell= (vec parameters))]
      (h/li (j/cell= (str (name key) ": " v)))))]))

(h/defelem shortcode [{:keys [content elem]}]
  (j/cell-let
   [{:keys [key tile_classes] n :name} elem]
   (h/div
    :class tile_classes
    (menus/module)
    (h/h3 (j/cell= (or n key)))
    (content))))

(h/defelem content [{{:keys [content]} :elem}]
  [(menus/module)
   (h/div content)])

(defn map-display [elems display]
  (when (and (-> elems empty? not)
             (-> display empty? not))
    (vec (map
          (fn [{:keys [key nodes element self-closing] :as e}]
            (let [r (if (-> nodes empty?)
                      e (assoc e :nodes (map-display nodes display)))]
              (if (= element :shortcode)
                (merge r (get display key))
                r)))
          elems))))

(h/defelem render [{:keys [elems]}]
  (h/div
   (h/loop-tpl
    :bindings [{:keys [element self-closing nodes] :as elem} elems]
    (h/div
     (h/cond-tpl
      (j/cell= (and (= element :shortcode) self-closing))
      (self-closing-shortcode :elem elem)
      (j/cell= (and (= element :shortcode) (not self-closing)))
      (shortcode :elem elem :content (render :elems nodes))
      (j/cell= (= element :content))
      (content :elem elem))))))

(h/defelem editor []
  (h/div
   :class "lsgc_editor_wrap"
   (menus/master)
   (render :elems (j/cell= (map-display s/main s/display-mods-map)))))

(j/cell= (.log js/console (clj->js (map-display s/main s/display-mods-map))))
