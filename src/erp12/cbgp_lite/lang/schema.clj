(ns erp12.cbgp-lite.lang.schema
  (:require [erp12.schema-inference.impl.util :as su]
            [clojure.walk :as w]
            [clojure.set :as set]))

(defn occurs?
  [term form]
  (let [t (transient #{})]
    (w/postwalk #(do (conj! t (= % term)) %) form)
    (contains? t true)))


(defn ground-type?
  "True if term is a ground type, false otherwise"
  [term]
  (and (map? term)
            (= 1 (count term))
            (symbol? (:type term))))

(defn all-ground-types
  "Returns all ground types out of nested form"
  [form]
  (let [ground-types (atom #{})]
    (w/postwalk #(do (when (ground-type? %)
                       (swap! ground-types conj %))
                     %)
                form)
    @ground-types))

(defn has-all-ground-types?
  "Checks if all ground types in form are contained in types"
  [types form]
  (set/subset? (all-ground-types form)
               (set types)))

(defn fn-arg-schemas
  [{:keys [type] :as schema}]
  (if (= type :scheme)
    (fn-arg-schemas (:body schema))
    (get-in schema [:input :children])))

(defn fn-ret-schema
  [{:keys [type] :as schema}]
  (if (= type :scheme)
    (fn-ret-schema (:body schema))
    (:output schema)))

(defn mgu
  [schema1 schema2]
  (su/mgu schema1 schema2))

(defn mgu-failure?
  [m]
  (su/mgu-failure? m))

(defn generalize
  [env schema]
  (su/generalize env schema))

(defn instantiate
  [schema]
  (su/instantiate schema))

(defn substitute
  [subs schema]
  (su/substitute subs schema))

(defn compose-substitutions
  [subs1 subs2]
  (su/compose-substitutions subs1 subs2))