(ns auth.views
    (:require
        [re-frame.core :as rf]
        [components.buttons :as buttons]
        [reagent-forms.core :refer [bind-fields]]
        [clairvoyant.core :refer-macros [trace-forms]]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "brown")}

(defn form-events
  [dbpath]
  {:get (fn [path] @(rf/subscribe [:form-field-value path]))
   :save! (fn [path value] (rf/dispatch [:set-form-value path value]))
   :update! (fn [path save-fn value]
              (rf/dispatch [:set-form-value path
                              (save-fn @(rf/subscribe [:form-field-value path]) value)]))
   :doc (fn [] @(rf/subscribe dbpath))})

(defn alert
  [alert-sub]
  (let [alert-data @(rf/subscribe alert-sub)]
    (when alert-data
      [:div {:class (str "card-alert alert alert-" (:type alert-data) " alert-dismissible mb-0")}
        [:button {:type "button" :class "close" :data-dismiss "alert"}]
        (:message alert-data)])))

(defn login-form []
  [:div {:class "container"}
    [:div {:class "row"}
      [:div {:class "col col-login mx-auto"}
        [:form {:class "card align-middle"}
          [:div {:class "card-header"}
            [:div {:class "mx-auto p-1"}
              [:img {:src "images/protos-logo.svg" :class "h-7" :alt "protos logo"}]]]
          [alert [:alert-login]]
          [:div {:class "card-body p-6"}
            [bind-fields
              [:div {:class "auto-form-wrapper"}
                [:div {:class "form-group"}
                  [:label {:class "form-label"} "Username"]
                  [:input {:field :text :id :login.form.username :class "form-control" :aria-describedby "usernameHelp" :placeholder "username"}]]
                [:div {:class "form-group"}
                  [:label {:class "form-label"} "Password"]
                  [:input {:field :password :id :login.form.password :class "form-control" :placeholder "password"}]]]
              (form-events [:init-form :step1])]
            (let [loading? @(rf/subscribe [:loading?])]
            [:div {:class "form-footer"}
              [buttons/submit-button-spinner "Login" [:login] "primary btn-block" loading? loading?]])]]]]])

)