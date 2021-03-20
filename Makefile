SHELL := /bin/bash

test:
	./gradlew clean test

build:
	./gradlew clean build

publish:
	./gradlew :otter:publish
	./gradlew :otter-translatable:publish
