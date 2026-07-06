#!/bin/sh
set -e

if [ -z "$(ls -A /app/uploads 2>/dev/null)" ]; then
  echo "Initializing uploads from demo-images..."
  cp -r /app/demo-images/. /app/uploads/
fi

exec java \
  -XX:+UseG1GC \
  -XX:MaxRAMPercentage=75.0 \
  -Djava.security.egd=file:/dev/./urandom \
  -jar app.jar \
  --spring.profiles.active=prod
