import pytest
import pynvim
from nvim_fixtures import get_nvim
import time


class TestDemoPluginFunctions(object):

    """
    TestDemoPluginFunctions.
    This test suite tests all demoplugin funtions by leveraging pynvim
    official client (since it's super stable).
    """

    def test_rpc_connection(self, get_nvim):
        """Test pynvim socket RPC connection."""
        assert get_nvim

    def test_greet(self, get_nvim):
        """Test demo-plugin Greet function."""
        nvim = get_nvim
        res = nvim.command_output("echo Greet('Scala')")
        assert res == "Hello Scala"

    def test_greet_wrong_args(self, get_nvim):
        """Test demo-plugin Greet function."""
        nvim = get_nvim
        with pytest.raises(pynvim.api.nvim.NvimError):
            nvim.command_output("echo Greet(1)")

    def test_greet_extra_args(self, get_nvim):
        """Test demo-plugin Greet function."""
        nvim = get_nvim
        with pytest.raises(pynvim.api.nvim.NvimError):
            nvim.command_output("echo Greet('Scala', 2)")

    def test_sum_from_until(self, get_nvim):
        """Test demoplugin SumFromUntil function."""
        nvim = get_nvim
        res = nvim.command_output("echo SumFromUntil(0, 10)")
        assert res == str(sum(range(0, 10)))

    def test_set_var_value_sync(self, get_nvim):
        """Test demo-plugin SetVarValueSync function."""
        nvim = get_nvim
        nvim.command("call SetVarValue(555)")
        res = nvim.command_output("echo g:test_var_value")
        assert res == "555"

    def test_set_var_value_async(self, get_nvim):
        """Test demo-plugin SetVarValueAsync function."""
        nvim = get_nvim
        nvim.command("call SetVarValueAsync(56)")
        # The async function does other things too, so wait 2 secs before checking
        time.sleep(2)
        res = nvim.command_output("echo g:test_var_value")
        assert res == "56"
