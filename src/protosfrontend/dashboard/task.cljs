(ns dashboard.task
    (:require
      [reagent.core :as r]
      [protosfrontend.util :as util]
      [components.buttons :as buttons]
      [reagent-forms.core :refer [bind-fields]]
      [re-frame.core :as rf]))

(defn alert [alert-sub]
  (let [alert-data @(rf/subscribe alert-sub)]
    (when alert-data
      [:div {:class (str "card-alert alert alert-" (:type alert-data) " alert-dismissible mb-0")}
        [:button {:type "button" :class "close" :data-dismiss "alert"}]
        (:message alert-data)])))

(defn tasks-page [title]
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
                [:th "State"]
                [:th "Progress"]
                [:th "Started at"]
                [:th "Finished at"]]]
            [:tbody
            (let [tasks @(rf/subscribe [:tasks])]
              (for [{id :id state :state progress :progress started-at :started-at finished-at :finised-at} (vals tasks)]
              [:tr {:key id}
                [:td {:class "text-center"}
                  [:div {:class "avatar d-block bg-white" :style {:background-image "url(images/task-generic.svg)" :background-size "80%"}}]]
                [:td
                  [:a {:href (str "/#/tasks/" id)} id]]
                [:td
                  [:div state]]
                [:td
                  [:div progress]]
                [:td
                  [:div started-at]]
                [:td
                  [:div finished-at]]]))]]]]]]])
