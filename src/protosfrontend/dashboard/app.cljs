(ns dashboard.app
    (:require
      [re-frame.core :as rf]
      [protosfrontend.util :as util]
      [protosfrontend.routes :as routes]
      [components.alerts :as alerts]
      [components.buttons :as buttons]
      [cljs-time.core :as tc]))

(defn apps-page [title]
  [:div {:class "container"}
    (if title
      [:div {:class "page-header"}
        [:h1 {:class "page-title" :style {:cursor "pointer"} :on-click #(rf/dispatch [:get-apps])} title ]])
    [alerts/for-list-page [:alert-dashboard]]
    [:div {:class "row row-cards row-deck"}
      [:div {:class "col-12"}
       [:div {:class "card"}
        [:div {:class "table-responsive"}
          [:table {:class "table table-hover table-outline table-vcenter text-nowrap card-table"}
            [:thead
              [:tr
                [:th {:class "text-center w-1"}]
                [:th "Name"]
                [:th "Task"]
                [:th {:class "text-center"} "Runtime"]
                [:th "Status"]
                [:th {:class "text-center"}
                  [:i {:class "fe fe-settings"}]]]]
            [:tbody
            (let [apps @(rf/subscribe [:apps])]
              (for [[app-id {name :name status :status id :id tasks :tasks}] apps
                    :let [last-task (last (vals tasks))
                          progress (:progress last-task)]]
                [:tr {:key app-id}
                  [:td {:class "text-center"}
                    [:div {:class "avatar d-block bg-white" :style {:background-image "url(/static/images/app-generic.svg)" :background-size "80%"}}
                      [:span {:class (str "avatar-status bg-" (util/app-status-color status))}]]]
                  [:td
                    [:a {:href (routes/url-for :app-page :id id)} name]
                    [:div {:class "small text-muted"} (str "ID: " id)]]
                  [:td
                    [:div {:class "clearfix"} [:div {:class "float-left"} [:strong (str (:percentage progress) "%")]]
                                              [:div {:class "float-right"} [:small {:class "text-muted"} (:name last-task)]]]
                    [:div {:class "progress progress-xs"}
                      [:div {:class "progress-bar bg-green"
                             :aria-valuemax "100"
                             :aria-valuemin "0"
                             :aria-valuenow (:percentage progress)
                             :style {:width (str (:percentage progress) "%")}
                             :role "progressbar"}]]]
                  [:td {:class "text-center"}
                    [:i {:class "tech tech-docker"}]]
                  [:td
                    [:div status]]
                  [:td {:class "text-center"}
                    [:div {:class "item-action dropdown"}
                      [:a {:data-toggle "dropdown" :class "icon"}
                        [:i {:class "fe fe-more-vertical"}]]
                      [:div {:class "dropdown-menu dropdown-menu-right"}
                        [:a {:href "javascript:void(0)" :on-click #(rf/dispatch [:app-state id "start"]) :class "dropdown-item"}
                          [:i {:class "dropdown-icon fe fe-play"}] " Start"]
                        [:a {:href "javascript:void(0)" :on-click #(rf/dispatch [:app-state id "stop"]) :class "dropdown-item"}
                          [:i {:class "dropdown-icon fe fe-stop-circle"}] " Stop"]
                        [:a {:href "javascript:void(0)" :on-click #(rf/dispatch [:remove-app id]) :class "dropdown-item"}
                          [:i {:class "dropdown-icon fe fe-trash"}] " Remove"]]]]]))]]]]]]])

(defn app-page [id]
  [:div {:class "container"}
    [:div {:class "row row-cards row-deck"}
      [:div {:class "col-12"}
        (let [app @(rf/subscribe [:app (keyword id)])
              loading? @(rf/subscribe [:loading?])
              time-now (tc/now)]
        [:div {:class "card"}
          [:div {:class "card-header"}
            [:div {:class "avatar d-block bg-white mr-3" :style {:background-image "url(/static/images/app-generic.svg)" :background-size "80%"}}
              [:span {:class (str "avatar-status bg-" (util/app-status-color (:status app)))}]]
            [:h3 {:class "card-title" :style {:cursor "pointer"} :on-click #(rf/dispatch [:get-app id])} (:name app)]
            [:div {:class "card-options"}
              [:div {:class "btn-list"}
                [buttons/submit-button "Start" [:app-state id "start"] "outline-success btn-sm" loading?]
                [buttons/submit-button "Stop" [:app-state id "stop"] "outline-primary btn-sm" loading?]
                [buttons/submit-button "Remove" [:remove-app id] "danger btn-sm" loading?]]]]
          [alerts/for-card [:alert-dashboard]]
          [:div {:class "card-body"}
            [:div {:class "row"}
              [:div {:class "col-6"}
                [:div {:class "row"}
                  [:div {:class "col-4"} [:strong "ID:"]]
                  [:div {:class "col-8"} (:id app)]]
                [:div {:class "row"}
                  [:div {:class "col-4"} [:strong "Installer ID:"]]
                  [:div {:class "col-8"} (:installer-id app)]]
                [:div {:class "row"}
                  [:div {:class "col-4"} [:strong "Status:"]]
                  [:div {:class "col-8"} [:span {:class (str "tag tag-rounded tag-" (util/app-status-color (:status app)))} (:status app)]]]
                [:div {:class "row"}
                  [:div {:class "col-4"} [:strong "IP:"]]
                  [:div {:class "col-8"} (:ip app)]]]
              [:div {:class "col-6"}
                [:div {:class "row"} [:strong "Tasks"]]
                [:ul {:class "timeline"}
                (for [[id {name :name status :status progress :progress finished-at :finished-at}] (rseq (:tasks app))]
                  [:li {:key id :class "timeline-item"}
                    [:div {:class (str "timeline-badge bg-" (util/task-status-color status))}] [:a {:href (routes/url-for :task-page :id id)} name]
                    (if finished-at
                      [:div {:class "timeline-time"} (str (util/formatted-interval finished-at time-now) " ago")]
                      [:div {:class "timeline-time col-2"}
                        [:div {:class "clearfix"} [:div {:class "float-left"} [:strong (str (:percentage progress) "%")]]]
                        [:div {:class "progress progress-xs"}
                          [:div {:class "progress-bar bg-green"
                                 :aria-valuemax "100"
                                 :aria-valuemin "0"
                                 :aria-valuenow (:percentage progress)
                                 :style {:width (str (:percentage progress) "%")}
                                 :role "progressbar"}]]])])]]]]])]]])
