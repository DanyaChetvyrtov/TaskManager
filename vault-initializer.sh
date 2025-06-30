#!/bin/bash
#!/bin/sh
set -e

# Настраиваем переменные окружения
export VAULT_ADDR="http://$VAULT_DEV_LISTEN_ADDRESS"
export VAULT_TOKEN="$VAULT_DEV_ROOT_TOKEN_ID"

# Включаем KV v2 secrets engine если ещё не включен
echo "Настройка Vault..."
vault secrets enable -path=secret kv-v2 || echo "Secrets engine already started"

# Создаём секреты
vault kv put secret/kv/auth-ms \
  DB_USERNAME="postgres" \
  DB_PASSWORD="root" \
  SECRET_KEY="aXQncyBzaW1wbGUgc2VjcmV0IGZvciBzdW1tZXIgcHJhY3RpY2UgWlVaRVggcHJvamVjdC4gVGFza01hbmFnZXI=" \
  ACCESS_DURATION=15 \
  REFRESH_DURATION=7

vault kv put secret/kv/account-ms \
  DB_USERNAME="postgres" \
  DB_PASSWORD="root" \
  SECRET_KEY="aXQncyBzaW1wbGUgc2VjcmV0IGZvciBzdW1tZXIgcHJhY3RpY2UgWlVaRVggcHJvamVjdC4gVGFza01hbmFnZXI="

vault kv put secret/kv/task-ms \
  DB_USERNAME="postgres" \
  DB_PASSWORD="root" \
  SECRET_KEY="aXQncyBzaW1wbGUgc2VjcmV0IGZvciBzdW1tZXIgcHJhY3RpY2UgWlVaRVggcHJvamVjdC4gVGFza01hbmFnZXI="

# Выводим информацию о созданных секретах
echo "Секреты успешно созданы:"
vault kv list secret

# Бесконечное ожидание чтобы контейнер не завершал работу
echo "Vault готов к использованию"
tail -f /dev/null
