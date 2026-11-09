# Two terminals' worth of work in one: `make dev` runs the API and the web app together.
.PHONY: dev api web test lint

dev: web/node_modules
	$(MAKE) -j2 api web

# Spring Boot's Docker Compose support starts Postgres from compose.yaml on bootRun.
api:
	# Some shells set DOCKER_DEFAULT_PLATFORM=linux/amd64, which breaks native images on Apple Silicon.
	cd api && env -u DOCKER_DEFAULT_PLATFORM ./gradlew bootRun

web: web/node_modules
	cd web && npm run dev

web/node_modules: web/package-lock.json
	cd web && npm ci
	touch web/node_modules

test: web/node_modules
	cd api && ./gradlew test
	cd web && npm test

lint: web/node_modules
	cd web && npm run lint && npm run format:check
