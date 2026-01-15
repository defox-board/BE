# BE

# devfox-board

## ローカル実行方法（Local Dev）

1. DB起動（Docker）  
   docker-compose -f docker-compose.local.yml up -d  
   （MySQL ポート：3307）

2. バックエンド起動（Spring Boot）  
   ./gradlew bootRun  
   （API Base URL：http://localhost:8083）

3. Swagger ドキュメント ....


## テストシナリオ資料一覧
- テストシナリオは以下のディレクトリを参照してください  
  [./docs](./docs)