```nginx
location /pub/(.+) {
    if ($http_origin ~ <允许的域（正则匹配）>) {
        add_header 'Access-Control-Allow-Origin' "$http_origin";
        add_header 'Access-Control-Allow-Credentials' "true";
        if ($request_method = "OPTIONS") {
            add_header 'Access-Control-Max-Age' 86400;
            add_header 'Access-Control-Allow-Methods' 'GET, POST, OPTIONS, DELETE';
            add_header 'Access-Control-Allow-Headers' 'reqid, nid, host, x-real-ip, x-forwarded-ip, event-type, event-id, accept, content-type';
            add_header 'Content-Length' 0;
             add_header 'Content-Type' 'text/plain, charset=utf-8';
             return 204;
         }
     }
     # 正常nginx配置
     ......
 }
```
