(ns dashboard.home
    (:require
      [clojure.string :as str]
      [re-frame.core :as rf]
      [components.cards :as cards]
      [components.alerts :as alerts]
      [protosfrontend.util :as util]))

(defn home-page [title]
  [:div {:class "page-main"}]
    [:div {:class "container"}
      (if title
        [:div {:class "page-header"} [:h1 {:class "page-title"} title]])
      [alerts/for-list-page [:alert-dashboard]]
      [:div {:class "row row-cards"}
        [:div {:class "col-lg-6"}
          [:div {:class "card"}
            [:div {:class "card-header"} [:h3 {:class "card-title"} "Public services"]]
            (let [services @(rf/subscribe [:services])]
            [:div {:class "card-body"}
              [:div {:class "table-responsive"}
                [:table {:class "table table-hover table-outline table-vcenter text-nowrap card-table"}
                  [:tbody
                  (for [{name :name ip :ip domain :domain ports :ports status :status} services]
                    [:tr {:key name}
                      [:td
                        [:div [:span {:class (str "status-icon bg-" (util/service-status-color status))}] name]]
                      [:td
                        [:div domain]
                        [:div {:class "small text-muted"} ip]]
                      [:td
                        [:div (str/join " " ports)]]])]]]])]]
        [:div {:class "col-lg-6"}
          [:div {:class "card"}
            [:div {:class "card-header"} [:h3 {:class "card-title"} "Hardware"]]]]]])