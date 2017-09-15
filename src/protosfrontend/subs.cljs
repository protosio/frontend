(ns protosfrontend.subs
    (:require
        [re-frame.core :as rf]
        [clairvoyant.core :refer-macros [trace-forms]]
        [re-frame-tracer.core :refer [tracer]]))


(trace-forms {:tracer (tracer :color "yellow")}

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
  (fn [db _]
    (-> db
        :show-create-app-modal)))

(rf/reg-sub
  :show-installer-metadata-modal
  (fn [db _]
    (-> db
        :show-installer-metadata-modal)))

;; -- Modal subs -----------------------------------------------

(rf/reg-sub
  :show-modal
  (fn [db _]
    (-> db
        :modal-data
        :show-modal)))

(rf/reg-sub
  :active-modal
  (fn [db _]
    (-> db
        :modal-data
        :active-modal)))

(rf/reg-sub
  :modal-params
  (fn [db _]
    (-> db
        :modal-data
        :modal-params)))

;; -- Form subs -----------------------------------------------

(rf/reg-sub
  :form-data
  (fn [db _]
    (-> db
        :form-data)))

)