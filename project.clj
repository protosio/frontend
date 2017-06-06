(defproject protosfrontend "0.1.0-SNAPSHOT"
  :description "Frontend for Protos"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.542"]
                 [org.clojure/core.async "0.3.443"]
                 [reagent "0.6.2"]
                 [reagent-utils "0.2.1"]
                 [reagent-forms "0.5.29"]
                 [secretary "1.2.3"]
                 [cljs-ajax "0.6.0"]
                 [figwheel-sidecar "0.5.10"]
                 [com.cemerick/piggieback "0.2.1"]
                 [leiningen-core "2.7.1"]
                 [re-frame "0.9.4"]
                 [day8.re-frame/http-fx "0.1.3"]]

  :plugins [[lein-cljsbuild "1.1.6"]
            [lein-figwheel "0.5.10"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:main protosfrontend.core
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/protosfrontend.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true}}
                       {:id "min"
                        :source-paths ["src"]
                        :compiler {:output-to "resources/public/js/compiled/protosfrontend.js"
                                   :main protosfrontend.core
                                   :optimizations :advanced
                                   :pretty-print false}}]}

  :figwheel {
             :css-dirs ["resources/public/css"] ;; watch and update CSS
             ;; Start an nREPL server into the running figwheel process
             :nrepl-port 7888})