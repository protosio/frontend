(ns dashboard.home
    (:require
      [clojure.string :as str]
      [re-frame.core :as rf]
      [components.cards :as cards]
      [components.alerts :as alerts]
      [protosfrontend.util :as util]))

(defn home-page [title]
  [:div {:class "page-main"}
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
            [:div {:class "card-header"} [:h3 {:class "card-title" :style {:cursor "pointer"} :on-click #(rf/dispatch [:get-hwstats])} "Hardware"]]
            (let [{{cpuusage :usage cpuinfo :info} :cpu memory :memory storage :storage} @(rf/subscribe [:hwstats])]
            [:div {:class "card-body"}
              [:div {:class "row m-2 align-items-center"}
                [:div {:class "col-md-3"}
                  [:div {:class (str "c100 " "p" cpuusage " small float-right")}
                    [:span  (str cpuusage "%")]
                    [:div {:class "slice"}
                      [:div {:class "bar"}]
                      [:div {:class "fill"}]]]]
                [:div {:class "col-md-9"}
                  [:div [:img {:src "/static/images/icons/protos-processor.svg" :class "h-5" :alt "cpu"}] [:b " CPU"]]
                  [:div (str (:cores cpuinfo) " x " (:model cpuinfo))]
                  [:div (str (:frequency cpuinfo) " MHz & " (:cache cpuinfo) " KB")]]]
              [:div {:class "row m-2 align-items-center"}
                [:div {:class "col-md-3"}
                  [:div {:class (str "c100 p" (:usage memory) " small float-right")}
                    [:span (str (:usage memory) "%")]
                    [:div {:class "slice"}
                      [:div {:class "bar"}]
                      [:div {:class "fill"}]]]]
                [:div {:class "col-md-9"}
                  [:div [:img {:src "/static/images/icons/protos-memory.svg" :class "h-5" :alt "cpu"}] [:b " Memory"]]
                  [:div (str "Total: " (:total memory) "MB Available: " (:available memory) "MB")]
                  [:div (str "Cached: " (:cached memory) "MB")]]]
              [:div {:class "row m-2 align-items-center"}
                [:div {:class "col-md-3"}
                [:div {:class (str "c100 p" (:usage storage) " small float-right")}
                  [:span (str (:usage storage) "%")]
                  [:div {:class "slice"}
                    [:div {:class "bar"}]
                    [:div {:class "fill"}]]]]
                [:div {:class "col-md-9"}
                [:div [:img {:src "/static/images/icons/protos-ssd.svg" :class "h-5" :alt "cpu"}] [:b " Storage"]]
                [:div (str "Total: " (:total storage) "MB Available: " (:available storage) "MB")]
                [:div (str "Path: \"" (:path storage) "\"")]]]])]]]]])
