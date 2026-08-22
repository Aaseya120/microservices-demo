#!/usr/bin/env bash
# Assembled from the guide's AWS Setup section (steps 3-4: ECR login,
# build, tag, push) and its GitHub Actions pipeline's image-loop, looped
# here over all 6 modules in this reactor rather than just the 4 named
# in the guide's CI job.
#
# Usage:
#   AWS_REGION=ap-south-1 \
#   ECR_REGISTRY=123456789.dkr.ecr.ap-south-1.amazonaws.com \
#   IMAGE_TAG=1.0.0 \
#   ./scripts/build-push-all.sh
set -euo pipefail

AWS_REGION="${AWS_REGION:-ap-south-1}"
ECR_REGISTRY="${ECR_REGISTRY:?Set ECR_REGISTRY, e.g. 123456789.dkr.ecr.ap-south-1.amazonaws.com}"
IMAGE_TAG="${IMAGE_TAG:-latest}"
SERVICES=(service-registry api-gateway user-service order-service product-service notification-service)

echo "Building the reactor once (produces every module's jar under target/)..."
mvn clean package -DskipTests

echo "Logging in to ECR ($ECR_REGISTRY)..."
aws ecr get-login-password --region "$AWS_REGION" | \
  docker login --username AWS --password-stdin "$ECR_REGISTRY"

for SERVICE in "${SERVICES[@]}"; do
  echo "── $SERVICE ──────────────────────────────────"
  aws ecr describe-repositories --repository-names "$SERVICE" --region "$AWS_REGION" >/dev/null 2>&1 \
    || aws ecr create-repository --repository-name "$SERVICE" --region "$AWS_REGION"

  docker build -f "$SERVICE/Dockerfile" -t "$SERVICE:$IMAGE_TAG" .
  docker tag "$SERVICE:$IMAGE_TAG" "$ECR_REGISTRY/$SERVICE:$IMAGE_TAG"
  docker push "$ECR_REGISTRY/$SERVICE:$IMAGE_TAG"
  docker tag "$SERVICE:$IMAGE_TAG" "$ECR_REGISTRY/$SERVICE:latest"
  docker push "$ECR_REGISTRY/$SERVICE:latest"
done

echo "All images pushed to $ECR_REGISTRY (tag: $IMAGE_TAG)."
