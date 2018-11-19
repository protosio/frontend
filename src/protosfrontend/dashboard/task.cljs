(ns dashboard.task
    (:require
      [reagent.core :as r]
      [protosfrontend.util :as util]
      [protosfrontend.routes :as routes]
      [components.buttons :as buttons]
      [components.alerts :as alerts]
      [reagent-forms.core :refer [bind-fields]]
      [cljs-time.core :as tc]
      [re-frame.core :as rf]))

(defn tasks-page [title]
  [:div {:class "container"}
    (if title
      [:div {:class "page-header"}
        [:h1 {:class "page-title" :style {:cursor "pointer"} :on-click #(rf/dispatch [:get-tasks])} title]])
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
                [:th {:class "text-center"} "Status"]
                [:th "Progress"]
                [:th "Started"]
                [:th "Finished"]]]
            [:tbody
            (let [tasks @(rf/subscribe [:tasks])
                  time-now (tc/now)]
              (for [{id :id name :name status :status progress :progress started-at :started-at finished-at :finished-at} (vals tasks)]
              [:tr {:key id}
                [:td {:class "text-center"}
                  [:div {:class "avatar d-block bg-white" :style {:background-image "url(/static/images/task-generic.svg)" :background-size "80%"}}]]
                [:td
                  [:a {:href (routes/url-for :task-page :id id)} name]
                  [:div {:class "small text-muted"} (str "ID: " id)]]
                [:td {:class "text-center"}
                  [:span {:class (str "status-icon bg-" (util/task-status-color status))}] (str " " status)]
                [:td
                  [:div {:class "clearfix"}
                   [:div {:class "float-left"} [:strong (str (:percentage progress) "%")]]]
                  [:div {:class "progress progress-xs"}
                   [:div {:class "progress-bar bg-green"
                          :aria-valuemax "100"
                          :aria-valuemin "0"
                          :aria-valuenow (:percentage progress)
                          :style {:width (str (:percentage progress) "%")}
                          :role "progressbar"}]]]
                [:td
                  [:div (util/time-str started-at)]]
                [:td
                  [:div (str (util/formatted-interval finished-at time-now) " ago")]]]))]]]]]]])

(defn task-page [id]
  [:div {:class "container"}
    [:div {:class "row row-cards row-deck"}
      [:div {:class "col-12"}
        (let [{id :id status :status progress :progress started-at :started-at finished-at :finished-at} @(rf/subscribe [:task (keyword id)])
              loading? @(rf/subscribe [:loading?])
              time-now (tc/now)]
        [:div {:class "card"}
          [:div {:class "card-header"}
            [:div {:class "avatar d-block bg-white mr-3" :style {:background-image "url(/static/images/task-generic.svg)" :background-size "80%"}}]
            [:h3 {:class "card-title" :style {:cursor "pointer"} :on-click #(rf/dispatch [:get-task id])} id]]
          [alerts/for-card [:alert-dashboard]]
          [:div {:class "card-body"}
            [:div {:class "row"}
              [:div {:class "col-lg-6"}
                [:div {:class "row"}
                  [:div {:class "col-sm-4"} [:strong "ID:"]]
                  [:div {:class "col-sm-8"} id]]
                [:div {:class "row"}
                  [:div {:class "col-sm-4"} [:strong "Status:"]]
                  [:div {:class "col-sm-8"} [:span {:class (str "status-icon bg-" (util/task-status-color status))}] (str " " status)]]
                [:div {:class "row"}
                  [:div {:class "col-sm-4"} [:strong "Progress:"]]
                  [:div {:class "col-sm-8 mb-2"}
                    [:div {:class "clearfix"} [:div {:class "float-left"} [:strong (str (:percentage progress) "%")]]]
                    [:div {:class "progress progress-xs"}
                      [:div {:class "progress-bar bg-green"
                             :aria-valuemax "100"
                             :aria-valuemin "0"
                             :aria-valuenow (:percentage progress)
                             :style {:width (str (:percentage progress) "%")}
                             :role "progressbar"}]]]]
                [:div {:class "row"}
                  [:div {:class "col-sm-4"} [:strong "Start time:"]]
                  [:div {:class "col-sm-8"} (util/time-str started-at)]]
                [:div {:class "row"}
                  [:div {:class "col-sm-4"} [:strong "End time:"]]
                  [:div {:class "col-sm-8"} (util/time-str finished-at)]]
                [:div {:class "row"}
                  [:div {:class "col-sm-4"} [:strong "Duration:"]]
                  [:div {:class "col-sm-8"} (util/formatted-interval started-at finished-at)]]
                [:div {:class "row"}
                  [:div {:class "col-sm-4"} [:strong "Status message:"]]
                  [:div {:class "col-sm-8"} (:state progress)]]]]]])]]])

