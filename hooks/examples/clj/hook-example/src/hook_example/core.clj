(ns hook-example.core
  "An example on how to implement hooks for CTIA"
  (require [ctia.flows.hook-protocol :refer [Hook]]))

(defrecord HookExample [n]
  Hook
  (init [_] :default)
  (destroy [_] :default)
  (handle [_ type-name stored-object prev-object]
    (into stored-object {n (str  "Passed in" n)})))

(def HookExample1 (HookExample. "HookExample1"))
(def HookExample2 (HookExample. "HookExample2"))
