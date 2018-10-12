(ns dashboard.resource
  (:require
    [re-frame.core :as rf]
    [protosfrontend.routes :as routes]
    [components.buttons :as buttons]
    [components.alerts :as alerts]
    [protosfrontend.util :as util]))

(defn resources-page [title]
  [:div {:class "container"}
    (if title
      [:div {:class "page-header"}
        [:h1 {:class "page-title" :on-click #(rf/dispatch [:get-resources])} title]])
    [alerts/for-list-page [:alert-dashboard]]
    [:div {:class "row row-cards row-deck"}
      [:div {:class "col-12"}
       [:div {:class "card"}
        [:div {:class "table-responsive"}
          [:table {:class "table table-hover table-outline table-vcenter text-nowrap card-table"}
            [:thead
              [:tr
                [:th {:class "text-center w-1"}
                  [:i {:class "icon-people"}]]
                [:th "ID"]
                [:th "Type"]
                [:th "Status"]
                [:th {:class "text-center"} [:i {:class "icon-settings"}]]]]
            [:tbody
          (let [resources @(rf/subscribe [:resources])]
            (for [{type :type id :id status :status app-id :app} (vals resources)]
              [:tr {:key id}
                [:td {:class "text-center"}
                  [:div {:class "avatar d-block bg-white" :style {:background-image "url(/static/images/resource-generic.svg)" :background-size "80%"}}]]
                [:td
                  [:a {:href (routes/url-for :resource-page :id id)} id]]
                [:td
                  [:div [:span {:class "tag"} type]]]
                [:td
                (if (= status "created")
                  [:div [:span {:class "status-icon bg-green"}] status]
                  [:div [:span {:class "status-icon bg-yellow"}] status])]
                [:td
                  [:div {:class "item-action dropdown"}
                    [:a {:href "javascript:void(0)" :data-toggle "dropdown" :class "icon"}
                      [:i {:class "fe fe-more-vertical"}]]
                    [:div {:class "dropdown-menu dropdown-menu-right"}
                      [:a {:href "javascript:void(0)" :class "dropdown-item"}
                        [:i {:class "dropdown-icon fe fe-trash"}] " Remove"]]]]]))]]]]]]])

(defn resource-page [id]
  [:div {:class "container"}
    [:div {:class "row row-cards row-deck"}
      [:div {:class "col-12"}
        (let [resource @(rf/subscribe [:resource (keyword id)])
              loading? @(rf/subscribe [:loading?])]
        [:div {:class "card"}
          [:div {:class "card-header"}
            [:div {:class "avatar d-block bg-white mr-3" :style {:background-image "url(/static/images/resource-generic.svg)" :background-size "80%"}}]
            [:h3 {:class "card-title" :on-click #(rf/dispatch [:get-resource id])} id]
            [:div {:class "card-options"}
              [:div {:class "btn-list"}
                [buttons/submit-button "Remove" [:remove-resource id] "danger btn-sm" loading?]]]]
          [alerts/for-card [:alert-dashboard]]
          [:div {:class "card-body"}
            [:div {:class "row mb-1"}
              [:div {:class "col-2"} [:strong "ID:"]]
              [:div {:class "col-5"} id]]
            [:div {:class "row mb-1"}
              [:div {:class "col-2"} [:strong "Type:"]]
              [:div {:class "col-5"} [:div [:span {:class "tag"} (:type resource)]]]]
            [:div {:class "row mb-1"}
              [:div {:class "col-2"} [:strong "Status:"]]
            (if (= (:status resource) "created")
              [:div {:class "col-5"} [:span {:class "status-icon bg-green"}] (:status resource)]
              [:div {:class "col-5"} [:span {:class "status-icon bg-yellow"}] (:status resource)])]
            [:div {:class "row mb-1"}
              [:div {:class "col-2"} [:strong "Value:"]]
              [:div {:class "col-5"} (:value resource)]]]])]]])
