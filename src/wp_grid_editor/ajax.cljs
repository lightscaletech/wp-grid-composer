(ns wp-grid-editor.ajax)

(defn- get-ajax-url [] (.-ajaxurl js/window))
(defn- add-action [action data] (assoc data :action (str "lsgc_" action)))

(defn post-cb [action data cb]
  (.post js/jQuery
         (get-ajax-url)
         (clj->js (add-action action data))
         #(cb (js->clj % :keywordize-keys true))))

(defn post-cell [act data cell] (post-cb act data #(reset! cell %)))
