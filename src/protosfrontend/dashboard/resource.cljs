(ns dashboard.resource
    (:require
      [re-frame.core :as rf]))

(defn resources-page [title]
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
                  [:th "ID"]
                  [:th "Type"]
                  [:th "Status"]
                  [:th {:class "text-center"} [:i {:class "icon-settings"}]]]]
              [:tbody
            (let [resources @(rf/subscribe [:resources])]
              (for [{type :type id :id status :status app-id :app} (vals resources)]
                [:tr {:key id}
                  [:td {:class "text-center"}
                    [:div {:class "avatar d-block bg-white" :style {:background-image "url(images/resource-generic.svg)" :background-size "80%"}}]]
                  [:td
                    [:a {:href (str "/#/resources/" id)} id]]
                  [:td
                    [:div [:span {:class "tag"} type]]]
                  [:td
                  (if (= status "created")
                    [:div [:span {:class "tag tag-green"} status]]
                    [:div [:span {:class "tag tag-yellow"} status]])]
                  [:td
                    [:div {:class "item-action dropdown"}
                      [:a {:href "javascript:void(0)" :data-toggle "dropdown" :class "icon"}
                        [:i {:class "fe fe-more-vertical"}]]
                      [:div {:class "dropdown-menu dropdown-menu-right"}
                        [:a {:href "javascript:void(0)" :class "dropdown-item"}
                          [:i {:class "dropdown-icon fe fe-trash"}] " Remove"]]]]]))]]]]]]])

(defn resource-page [id]
    [:div {:class "row"}
        [:div {:class "col-lg-12 grid-margin stretch-card"}
            [:div {:class "card"}
            (let [resources @(rf/subscribe [:resources])
                  resource (get resources (keyword id))
                  resource-id (:id resources)]
                [:div {:class "card-body"}
                  [:h4 {:class "card-title"} (:id resource)]
                  [:div.resource-details
                    [:div.row
                    [:div.col-md-12
                      [:div.table-responsive
                        [:table {:class "table table-striped table-bordered"}
                          [:tbody
                          [:tr
                            [:th "Type"]
                            [:td (:type resource)]]
                          [:tr
                            [:th "App"]
                            [:td (:app resource)]]
                          [:tr
                            [:th "Status"]
                            [:td (:status resource)]]]]]]]]])]]])
