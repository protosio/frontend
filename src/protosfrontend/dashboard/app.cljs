(ns dashboard.app
    (:require
      [re-frame.core :as rf]
      [protosfrontend.util :as util]))

(defn alert [alert-sub]
  (let [alert-data @(rf/subscribe alert-sub)]
    (when alert-data
      [:div {:class (str "card-alert alert alert-" (:type alert-data) " alert-dismissible mb-0")}
        [:button {:type "button" :class "close" :data-dismiss "alert"}]
        (:message alert-data)])))

(defn apps-page [title]
  [:div {:class "container"}
    (if title
      [:div.page-header [:h1.page-title title]])
    [:div {:class "row row-cards row-deck"}
      [:div {:class "col-12"}
       [:div {:class "card"}
        [:div {:class "table-responsive"}
          [:table {:class "table table-hover table-outline table-vcenter text-nowrap card-table"}
            [:thead
              [:tr
                [:th {:class "text-center w-1"}
                  [:i {:class "icon-people"}]]
                [:th "Name"]
                [:th "Last activity"]
                [:th {:class "text-center"} "Runtime"]
                [:th "Status"]
                [:th {:class "text-center"}
                  [:i {:class "icon-settings"}]]]]
            [:tbody
            (let [apps @(rf/subscribe [:apps])]
              (for [{name :name, id :id, status :status} (vals apps)]
              [:tr {:key id}
                [:td {:class "text-center"}
                  [:div {:class "avatar d-block" :style {:background-image "url(images/app-generic.png)"}}
                    [:span {:class "avatar-status bg-green"}]]]
                [:td
                  [:a {:href (str "/#/apps/" id)} name]
                  [:div {:class "small text-muted"} (str "ID: " id)]]
                [:td
                  [:div {:class "small text-muted"} "Application started"]
                  [:div "5 minutes ago"]]
                [:td {:class "text-center"}
                  [:i {:class "tech tech-docker"}]]
                [:td
                  [:div status]]
                [:td
                  [:div {:class "item-action dropdown"}
                    [:a {:href "javascript:void(0)" :data-toggle "dropdown" :class "icon"}
                      [:i {:class "fe fe-more-vertical"}]]
                    [:div {:class "dropdown-menu dropdown-menu-right"}
                      [:a {:href "javascript:void(0)" :class "dropdown-item"}
                        [:i {:class "dropdown-icon fe fe-play"}] " Start"]
                      [:a {:href "javascript:void(0)" :class "dropdown-item"}
                        [:i {:class "dropdown-icon fe fe-stop-circle"}] " Stop"]
                      [:a {:href "javascript:void(0)" :class "dropdown-item"}
                        [:i {:class "dropdown-icon fe fe-trash"}] " Remove"]]]]]))]]]]]]])

(defn app-page [id]
  [:div {:class "container"}
    [:div {:class "row row-cards row-deck"}
      [:div {:class "col-12"}
        (let [app @(rf/subscribe [:app (keyword id)])
              loading? @(rf/subscribe [:loading?])]
        [:div {:class "card"}
          [:div {:class "card-header"}
            [:div {:class "avatar d-block mr-3" :style {:background-image "url(images/app-generic.png)"}}
              [:span {:class "avatar-status bg-green"}]]
            [:h3 {:class "card-title"} (:name app) (when loading? [:i {:class "fa fa-spin fa-circle-o-notch"}])]
            [:div {:class "card-options"}
              [:div {:class "btn-list"}
                [util/submit-button "Start" [:app-state id "start"] "success btn-sm" loading?]
                [util/submit-button "Stop" [:app-state id "stop"] "primary btn-sm" loading?]
                [util/submit-button "Remove" [:remove-app id] "danger btn-sm" loading?]
                [util/submit-button [:i {:class "fe fe-refresh-ccw"}] [:get-app id] "primary btn-sm btn-icon" loading?]]]]
          [alert [:alert-dashboard]]
          [:div {:class "card-body"}
            [:div {:class "row"}
              [:div {:class "col-2"} [:strong "ID:"]]
              [:div {:class "col-5"} (:id app)]]
            [:div {:class "row"}
              [:div {:class "col-2"} [:strong "Installer ID:"]]
              [:div {:class "col-5"} (:installer-id app)]]
            [:div {:class "row"}
              [:div {:class "col-2"} [:strong "Status:"]]
              [:div {:class "col-5"} (:status app)]]]])]]])
