(ns user
  ;;(:require [flames.core :as flames])
  )

(comment
  (def flames (flames/start! {:port 54321
                              :host "localhost"})))
(comment (flames/stop! flames))
