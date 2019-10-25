(ns protosfrontend.components.alerts
    (:require
      [re-frame.core :as rf]))

(defn for-list-page [alert-sub]
  (let [alert-data @(rf/subscribe alert-sub)]
    (when alert-data
      [:div {:class (str "alert alert-" (:type alert-data) " alert-dismissible mb-1")}
        [:button {:type "button" :class "close" :data-dismiss "alert"}]
        (:message alert-data)])))

(defn for-card [alert-sub]
  (let [alert-data @(rf/subscribe alert-sub)]
    (when alert-data
      [:div {:class (str "card-alert alert alert-" (:type alert-data) " alert-dismissible mb-1")}
        [:button {:type "button" :class "close" :data-dismiss "alert"}]
        (:message alert-data)])))