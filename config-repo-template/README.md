# SmartCourier Config Repository Template

Copy these YAML files into a separate Git repository and point `config-server` to that repository using `CONFIG_REPO_URI`.

Recommended repository layout:

```text
smart-courier-config/
├── admin-service.yml
├── auth-service.yml
├── config-server.yml
├── delivery-service.yml
├── discovery-server.yml
├── gateway-service.yml
└── tracking-service.yml
```

Keep secrets out of Git by using environment placeholders such as `${DB_PASSWORD}` and `${JWT_SECRET}`.
