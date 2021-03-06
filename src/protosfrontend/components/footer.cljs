(ns protosfrontend.components.footer
  (:require
    [re-frame.core :as rf]))

(defn bar []
  [:footer {:class "footer"}
    [:div {:class "container"}
      [:div {:class "row align-items-center flex-row-reverse"}
        [:div {:class "col-auto ml-lg-auto"}
          [:div {:class "row align-items-center"}
            [:div {:class "col-auto"}
              [:a {:target "_blank" :href "https://releases.protos.io"} @(rf/subscribe [:instance-info])]]
            [:div {:class "col-auto"}
              [:a {:class "btn btn-outline-primary btn-sm" :target "_blank" :href "https://code.protos.io"} "Source code"]]]]
        [:div {:class "col-12 col-lg-auto mt-3 mt-lg-0 text-center"} "Copyright © 2018-2019 "
          [:a {:target "_blank" :href "https://www.protos.io"} "Protos"]]]]])
