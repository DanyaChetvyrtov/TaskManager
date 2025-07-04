#!/bin/sh

# Запускаем Vault в фоновом режиме (в dev-режиме)
vault server -dev -dev-listen-address=0.0.0.0:8200 -dev-root-token-id=myroot &

# Ждём, пока Vault станет доступен
while ! nc -z 0.0.0.0 8200; do
  sleep 1
done

# Выполняем скрипт инициализации
if [ -f "/usr/local/bin/vault-initializer.sh" ]; then
  sh /usr/local/bin/vault-initializer.sh
fi

# Оставляем контейнер работающим
tail -f /dev/null