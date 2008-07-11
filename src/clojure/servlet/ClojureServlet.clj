(in-ns 'user)
(clojure/refer 'clojure)

(defn handle-request [request response]
  "<h1>Hello, World!</h1>")

(in-ns 'clojure.servlet.ClojureServlet)
(clojure/refer 'clojure)

(import '(javax.servlet.http HttpServlet) 
	'(java.net ServerSocket SocketException)
        '(java.io InputStreamReader OutputStreamWriter)
        '(clojure.lang LineNumberingPushbackReader))

(defn on-thread [f]
  (doto (new Thread f) (start)))

(defn create-server 
  "creates and returns a server socket on port, will pass the client
  socket to accept-socket on connection" 
  [accept-socket port]
    (let [ss (new ServerSocket port)]
      (on-thread #(when-not (. ss (isClosed))
                    (try (accept-socket (. ss (accept)))
                         (catch SocketException e))
                    (recur)))
      ss))

(defn repl
  "runs a repl on ins and outs until eof"
  [ins outs]
    (binding [*ns* (create-ns 'user)
              *warn-on-reflection* false
              *out* (new OutputStreamWriter outs)]
      (let [eof (new Object)
            r (new LineNumberingPushbackReader (new InputStreamReader ins))]
	(print (str (ns-name *ns*) "=> "))
	(flush)
        (loop [e (read r false eof)]
          (when-not (= e eof)
            (prn (eval e))
	    (print (str (ns-name *ns*) "=> "))
            (flush)
            (recur (read r false eof)))))))

(defn socket-repl 
  "starts a repl thread on the iostreams of supplied socket"
  [s] (on-thread #(repl (. s (getInputStream)) (. s (getOutputStream)))))


(defn init-void [this] 
  (create-server socket-repl 13579)
  (println "ClojureServlet initialized"))

(defn service-HttpServletRequest-HttpServletResponse [this request response]
  (.write (.getWriter response) (user/handle-request request response)))

