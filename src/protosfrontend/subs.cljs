(ns protosfrontend.subs
    (:require
        [re-frame.core :as rf]
        [clairvoyant.core :refer-macros [trace-forms]]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "blue")}

;; -- Queries -----------------------------------------------



(rf/reg-sub
  :apps
  (fn apps-sub
    [db _]
    (-> db
        :apps)))

(rf/reg-sub
  :installers
  (fn installers-sub
    [db _]
    (-> db
        :installers)))

(rf/reg-sub
  :installer
  (fn installer-sub
    [db [_ installer-id]]
    (get-in db [:installers (keyword installer-id)])))

(rf/reg-sub
  :installer-params
  (fn installer-params-sub
    [db [_ installer-id]]
    (get-in db [:installers (keyword installer-id) :metadata :params])))

(rf/reg-sub
  :resources
  (fn resources-sub
    [db _]
    (-> db
        :resources)))

(rf/reg-sub
  :active-page
  (fn active-page-sub
    [db _]
    (-> db
        :active-page)))

(rf/reg-sub
  :show-create-app-modal
  (fn show-create-app-modal-sub
    [db _]
    (-> db
        :show-create-app-modal)))

(rf/reg-sub
  :show-installer-metadata-modal
  (fn show-installer-metadata-modal-sub
    [db _]
    (-> db
        :show-installer-metadata-modal)))

;; -- Modal subs -----------------------------------------------

(rf/reg-sub
  :show-modal
  (fn show-modal-sub
    [db _]
    (-> db
        :modal-data
        :show-modal)))

(rf/reg-sub
  :active-modal
  (fn active-modal-sub
    [db _]
    (-> db
        :modal-data
        :active-modal)))

(rf/reg-sub
  :modal-params
  (fn modal-params-sub
    [db _]
    (-> db
        :modal-data
        :modal-params)))

;; -- Form subs -----------------------------------------------

(rf/reg-sub
  :form-data
  (fn form-data-sub
    [db _]
      (:form-data db)))

(rf/reg-sub
  :form-field-value
  :<- [:form-data]
  (fn form-field-value-sub
    [doc [_ path]]
      (get-in doc path)))

;; -- Loading subs -----------------------------------------------

(rf/reg-sub
  :loading?
  (fn loading-sub
    [db _]
      (:loading? db)))

;; -- Alert sub -----------------------------------------------

(rf/reg-sub
  :alert
  (fn alert-sub
    [db _]
      (-> db
          :alert
          :main)))


;; -- Auth data subs -----------------------------------------------

(rf/reg-sub
  :auth-data
  (fn auth-data-sub
    [db _]
      (:auth db)))

(rf/reg-sub
  :username
  (fn username-sub
    [db _]
    (-> db
        :auth
        :username)))

)