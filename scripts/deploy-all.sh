#!/usr/bin/env bash
# Assembled from the guide's "kubectl — Essential Commands" section,
# applying manifests in dependency order (namespace, secrets/config
# before anything that references them, then deployments, then ingress).
#
# Usage: ./scripts/deploy-all.sh
set -euo pipefail

echo "Applying namespace..."
kubectl apply -f k8s/namespaces/namespace.yaml

echo "Applying secrets (plaintext demo versions — swap for sealed-secrets in prod)..."
kubectl apply -f k8s/secrets/db-secret.yaml
kubectl apply -f k8s/secrets/jwt-secret.yaml

echo "Applying ConfigMap..."
kubectl apply -f k8s/configmaps/app-config.yaml

echo "Applying deployments (registry first so services have something to register with)..."
kubectl apply -f k8s/deployments/service-registry.yaml
kubectl rollout status deployment/service-registry -n microservices

kubectl apply -f k8s/deployments/user-service.yaml
kubectl apply -f k8s/deployments/product-service.yaml
kubectl apply -f k8s/deployments/order-service.yaml
kubectl apply -f k8s/deployments/notification-service.yaml
kubectl apply -f k8s/deployments/api-gateway.yaml

echo "Applying ingress..."
kubectl apply -f k8s/ingress/ingress.yaml

echo "Waiting for rollouts..."
for d in user-service product-service order-service notification-service api-gateway; do
  kubectl rollout status "deployment/$d" -n microservices
done

echo "Done. Pods:"
kubectl get pods -n microservices
