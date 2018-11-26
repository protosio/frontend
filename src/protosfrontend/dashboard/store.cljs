(ns dashboard.store
  (:require
    [reagent.core :as r]
    [re-frame.core :as rf]
    [protosfrontend.util :as util]
    [protosfrontend.routes :as routes]
    [components.buttons :as buttons]
    [components.alerts :as alerts]
    [reagent-forms.core :refer [bind-fields]]))

(defn store-page [title]
  [:div {:class "container"}
  (if title
    [:div {:class "page-header"} [:h1 {:class "page-title"} title]])
    [alerts/for-list-page [:alert-dashboard]]
    [:div {:class "row row-cards"}
      ;; Filter card
      [:div {:class "col-lg-3"}
        [:div {:class "card"}
          [:div {:class "card-body"}
            [:div {:class "auto-form-wrapper"}
              [bind-fields
                [:div
                [:div {:class "form-group"}
                  [:label {:class "form-label"} "Filter"]
                  [:input {:field :text :id :store.filter :class "form-control" :placeholder "term"}]]]
                (util/form-events [:store-filter-form])]
              [:div {:class "form-footer text-right"}
              (let [loading? @(rf/subscribe [:loading?])]
                [buttons/submit-button-spinner "Filter" [:search-appstore] "outline-primary btn-sm mr-2" loading? loading?])]]]]]
      ;; Application cards
      [:div {:class "col-lg-9"}
        [:div {:class "row"}
        (let [installers @(rf/subscribe [:store-installers])]
          (for [[id {name :name versions :versions}] (seq installers)]
          [:div {:key id :class "col-lg-4"}
            [:div {:class "card p-3"}
              [:div {:class "d-flex align-items-center"}
                [:img {:class "d-flex mr-3 rounded" :src "/static/images/installer-generic.svg" :alt name :width "25%"}]
                [:div
                  [:h6 {:class "m-0"} [:a {:href (routes/url-for :store-installer-page :id id)} name]]
                  [:small {:class "text-muted"} (util/trunc (:description (get versions (last (sort (keys versions))))) 60)]]]]]))]]]])

(defn install-app [name metadata]
  [:div {:class "card-body"}
    [:div {:class "row justify-content-center"}
      [:div {:class "auto-form-wrapper col-md-6"}
        [bind-fields
          [:div
          [:div {:class "form-group"}
            [:label {:class "form-label"} "Application name"]
            [:input {:field :text :id :create-app.form.name :class "form-control" :placeholder name}]]]
          (util/form-events [:create-app-form])]]]
  (if-not (empty? (:params metadata))
    [:div {:class "row justify-content-center"}
      [:div {:class "auto-form-wrapper col-md-6"}
        [:div {:class "border-top my-3"}]
        [bind-fields
          (into [:div ] (for [field (:params metadata)]
                             [:div {:key field :class "form-group"}
                               [:label {:class "form-label"} field]
                               [:input {:field :text :id (keyword (str "create-app.form.installer-params." field)) :class "form-control" :placeholder field}]]))
          (util/form-events [:create-app-form])]]])])

(defn installer-details [id name version metadata]
  [:div {:class "card-body"}
    [:div {:class "row mb-1"}
      [:div {:class "col-2"} [:strong "Description:"]]
      [:div {:class "col-5"} (:description metadata)]]
    [:div {:class "row mb-1"}
      [:div {:class "col-2"} [:strong "ID:"]]
      [:div {:class "col-5"} id]]
    [:div {:class "row mb-1"}
      [:div {:class "col-2"} [:strong "Provides:"]]
      [:div {:class "col-5"} (for [type (:provides metadata)]
                                  [:span {:key type :class "tag"} type])]]])

(defn store-installer-page [id]
  (let [installer @(rf/subscribe [:store-installer id])
        versions (keys (:versions installer))
        selected-version (r/atom (last (sort versions)))
        metadata (get-in installer [:versions @selected-version])
        loading? @(rf/subscribe [:loading?])
        page-choice (r/atom "details")]
    (fn store-installer-renderer []
      [:div {:class "container"}
        [alerts/for-card [:alert-dashboard]]
        [:div {:class "row row-cards row-deck"}
          [:div {:class "col-12"}
            [:div {:class "card"}
              [:div {:class "card-header"}
                [:div {:class "avatar d-block bg-white mr-3" :style {:background-image "url(/static/images/installer-generic.svg)" :background-size "80%"}}]
                [:h3 {:class "card-title" :style {:cursor "pointer"}} (:name installer)]
                [:form
                  [:select {:class "btn btn-secondary dropdown-toggle ml-2" :value @selected-version :on-change #(reset! selected-version (-> % .-target .-value))}
                  (for [version versions]
                    [:option {:key version :value version} version])]]
                [:div {:class "card-options"}
                  (if (= @page-choice "details")
                  [:div {:class "btn-list"}
                    [:button {:type "button" :class "btn btn-outline-primary btn-sm" :on-click #(reset! page-choice "create-app")} "Install app"]]
                  [:div {:class "btn-list"}
                    [buttons/submit-button "Install" [:create-app (:id installer) @selected-version] "primary btn-sm" loading?]
                    [:button {:type "button" :class "btn btn-outline-danger btn-sm" :on-click #(reset! page-choice "details")} "Cancel"]])]]
            (if (= @page-choice "details")
              [installer-details id (:name installer) @selected-version metadata]
              [install-app (:name installer) metadata])]]]])))
