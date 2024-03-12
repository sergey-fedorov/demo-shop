#!/bin/bash

if [ -z $1 ] || [ -z $2 ]
then
  echo "Some or all of the parameters are empty";
  echo "Usage: $0 projectKey token"
  echo -e "\t- projectKey jira project key for tests "
  echo -e "\t- token Public REST API token for Zephyr Scale"
  exit 1
fi

PROJECT_KEY=$1 # project key
TOKEN=$2 # Public REST API token

URL="https://api.zephyrscale.smartbear.com/v2/automations/executions/junit?projectKey=${PROJECT_KEY}&autoCreateTestCases=false"

zip -D ./target/junit_tests.zip ./target/surefire-reports/TEST-com.demo.shop.tests.e2e*.xml

curl -o -X POST -F "file=@target/junit_tests.zip" -F "testCycle={\"name\": \"E2E tests execution on $(date)\"};type=application/json" -H "Authorization: Bearer ${TOKEN}" $URL