(ns dashboard.store
  (:require
    [re-frame.core :as rf]
    [protosfrontend.util :as util]
    [components.buttons :as buttons]
    [reagent-forms.core :refer [bind-fields]]
    [cljsjs.lunrjs]))


(defn alert [alert-sub]
  (let [alert-data @(rf/subscribe alert-sub)]
    (when alert-data
      [:div {:class (str "card-alert alert alert-" (:type alert-data) " alert-dismissible mb-2")}
        [:button {:type "button" :class "close" :data-dismiss "alert"}]
        (:message alert-data)])))

(defn store-page [title]
  [:div {:class "container"}
  (if title
    [:div.page-header [:h1.page-title title]])
    [alert [:alert-dashboard]]
    [:div {:class "row row-cards row-deck"}
      ;; Filter card
      [:div {:class "col-sm-3"}
        [:div.card
          [:div.card-body
            [:div {:class "auto-form-wrapper"}
              [bind-fields
                [:div
                [:div {:class "form-group"}
                  [:label {:class "form-label"} "Filter"]
                  [:input {:field :text :id :store.filter :class "form-control" :placeholder "term"}]]]
                (util/form-events [:store-filter-form])]
              [:div.form-footer.text-right
              (let [loading? @(rf/subscribe [:loading?])]
                [buttons/submit-button-spinner "Filter" [:search-appstore] "outline-primary btn-sm mr-2" loading? loading?])]]]]]
      ;; Application cards
      [:div {:class "col-sm-9"}
        [:div {:class "row"}
        (let [installers @(rf/subscribe [:store-installers])]
          (for [[id {name :name description :description}] (seq installers)]
          [:div {:key id :class "col-sm-4"}
            [:div.card.p-3
              [:div.d-flex.align-items-center
                [:img {:class "d-flex mr-3 rounded" :src "images/installer-generic.svg" :alt "installer image" :width "25%"}]
                [:div
                  [:h6.m-0 [:a {:href (str "/#/store/" (subs (str id) 1))} name]]
                  [:small.text-muted (util/trunc description 60)]]]]]))]]]])

(defn installer-details [id installer]
  (let [versions (:versions installer)
        selected-version (last (sort versions))]
  [:div {:class "card-body"}
    [:div {:class "row mb-1"}
      [:div {:class "col-2"} [:strong "Description:"]]
      [:div {:class "col-5"} (:description installer)]]
    [:div {:class "row mb-1"}
      [:div {:class "col-2"} [:strong "ID:"]]
      [:div {:class "col-5"} id]]
    [:div {:class "row mb-1"}
      [:div {:class "col-2"} [:strong "Versions:"]]
      [:div {:class "col-5"} (for [version versions]
                                  [:button {:type "button" :key version :class (str "btn btn-sm mr-1 "  (if (= selected-version version) "btn-primary" "btn-secondary"))} version])]]
    [:div {:class "row mb-1"}
      [:div {:class "col-2"} [:strong "Provides:"]]
      [:div {:class "col-5"} (for [type (:provides installer)]
                                  [:span {:key type :class "tag"} type])]]]))

(defn store-installer-page [id]
  [:div {:class "container"}
    [alert [:alert-dashboard]]
    [:div {:class "row row-cards row-deck"}
      [:div {:class "col-12"}
        (let [installer @(rf/subscribe [:store-installer id])
              versions (:versions installer)
              selected-version (last (sort versions))
              loading? @(rf/subscribe [:loading?])]
        [:div {:class "card"}
          [:div {:class "card-header"}
            [:div {:class "avatar d-block bg-white mr-3" :style {:background-image "url(images/installer-generic.svg)" :background-size "80%"}}]
            [:h3 {:class "card-title"} (:name installer) (when loading? [:i {:class "fa fa-spin fa-circle-o-notch"}])]
            [:div {:class "card-options"}
              [:div {:class "btn-list"}
                [:button {:type "button" :class "btn btn-primary btn-sm" :on-click #(rf/dispatch [:download-installer id])} "Download"]]]]
          [installer-details id installer]])]]])