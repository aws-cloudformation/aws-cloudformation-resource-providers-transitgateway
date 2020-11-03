alias start-lamda='sam local start-lambda'

cfn_test_fast()
{
	MESSAGE="${1}"
	if [ "$MESSAGE" = "" ]; then
    echo "ERROR: You must provide a file name WITHOUT .json"
    echo "Run again with one of the following arguments:"
    echo $(ls sam-test)  | sed -e "s/\.json//g"
  else
	  sam local invoke TestEntrypoint --event sam-test/${1}.json
	fi
}

cfn_test()
{
	MESSAGE="${1}"
	if [ "$MESSAGE" = "" ]; then
    echo "ERROR: You must provide a file name WITHOUT .json"
    echo "Run again with one of the following arguments:"
    echo $(ls sam-test)  | sed -e "s/\.json//g"
  else
		mvn package && cfn_test_fast $1
	fi
}

cfn_unit_test()
{
	mvn package -Dmaven.install.skip=true
}

cfn_test_pretty()
{
	cfn_test $1 > sam-test/tmp.txt
	tail -1 sam-test/tmp.txt | jq
	rm sam-test/tmp.txt
}

cfn_test_fast_pretty()
{
	cfn_test_fast $1 > sam-test/tmp.txt
	tail -1 sam-test/tmp.txt | jq
	rm sam-test/tmp.txt
}
