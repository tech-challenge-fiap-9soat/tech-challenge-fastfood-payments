#!/bin/bash

# URL do endpoint
url="http://localhost:30001/fastfood/pedido"

# Loop para fazer 1000 requisições
for i in {1..1000}
do
  echo "Fazendo requisição $i..."
  curl -X 'GET' \
    "$url" \
    -H 'accept: */*' &
done

# Esperar todas as requisições terminarem
wait

echo "1000 requisições completadas."
