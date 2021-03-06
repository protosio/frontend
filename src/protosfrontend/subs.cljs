(ns protosfrontend.subs
    (:require
        [re-frame.core :as rf]))

;; -- Queries -----------------------------------------------

(rf/reg-sub
  :active-page
  (fn active-page-sub
    [db _]
    (-> db
        :active-page)))

(rf/reg-sub
  :instance-info
  (fn instance-info-sub
    [db _]
    (let [version (get-in db [:instance-info :version])]
         (when version
            (str "v" version)))))

;; -- Form subs -----------------------------------------------

(rf/reg-sub
  :form-data
  (fn form-data-sub
    [db _]
      (:form-data db)))

(rf/reg-sub
  :form-field-value
  (fn form-field-value-sub
    [db [_ path]]
      (get-in db path)))

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
  :userinfo
  (fn userinfo-sub
    [db _]
    (-> db
        :auth
        :userinfo)))

;; -- WS queries -----------------------------------------------

(rf/reg-sub
  :ws-connected
  (fn ws-connected-sub
    [db _]
      (:ws-connected db)))
