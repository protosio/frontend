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
        [:h1 {:class "page-title" :style {:cursor "pointer"} :on-click #(rf/dispatch [:get-resources])} title]])
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
                [:th {:class "text-center"} [:i {:class "fe fe-settings"}]]]]
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
                  [:div [:span {:class (str "status-icon bg-" (util/resource-status-color status))}] status]]
                [:td {:class "text-center"}
                  [:div {:class "item-action dropdown"}
                    [:a {:data-toggle "dropdown" :class "icon"}
                      [:i {:class "fe fe-more-vertical"}]]
                    [:div {:class "dropdown-menu dropdown-menu-right"}
                      [:a {:href "javascript:void(0)" :on-click #(rf/dispatch [:remove-resource id]) :class "dropdown-item"}
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
            [:h3 {:class "card-title" :style {:cursor "pointer"} :on-click #(rf/dispatch [:get-resource id])} id]
            [:div {:class "card-options"}
              [:div {:class "btn-list"}
                [buttons/submit-button "Remove" [:remove-resource id] "danger btn-sm" loading?]]]]
          [alerts/for-card [:alert-dashboard]]
          [:div {:class "card-body"}
            [:div {:class "row"}
              [:div {:class "col-lg-6"}
                [:div {:class "row mb-1"}
                  [:div {:class "col-sm-4"} [:strong "ID:"]]
                  [:div {:class "col-sm-8"} id]]
                [:div {:class "row mb-1"}
                  [:div {:class "col-sm-4"} [:strong "Type:"]]
                  [:div {:class "col-sm-8"} [:div [:span {:class "tag"} (:type resource)]]]]
                [:div {:class "row mb-1"}
                  [:div {:class "col-sm-4"} [:strong "Status:"]]
                  [:div {:class "col-sm-8"} [:span {:class (str "status-icon bg-" (util/resource-status-color (:status resource)))}] (:status resource)]]
                [:div {:class "row mb-1"}
                  [:div {:class "col-sm-4"} [:strong "Value:"]]
                  [:div {:class "col-sm-8"} (:value resource)]]]]]])]]])
