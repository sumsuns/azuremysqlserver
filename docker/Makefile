include INFO

.PHONY: build test push test clean release save

build:
	sudo docker build $(DOCKER_BUILD_OPTS) -t $(TMP_IMAGE_NAME) .

save:
	sudo docker save $(TMP_IMAGE_NAME) > ../images/$(IMAGE_NAME):$(TAG).tar

push:
	sudo docker tag $(TMP_IMAGE_NAME) $(NS_NAME_TAG)
	sudo docker push $(NS_NAME_TAG)

test:
	sudo docker run --rm  --entrypoint=bash --name $(CONTAINER) $(TMP_IMAGE_NAME)

clean:
	rm -f ../images/$(IMAGE_NAME):$(TAG).tar

release: build test push

default: build
