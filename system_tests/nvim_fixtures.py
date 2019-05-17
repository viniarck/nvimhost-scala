import os
import subprocess
import time
import pytest
import pynvim

# mock nvim in a new address in case you're already running in your machine
nvim_test_addr = "127.0.0.1:7777"
manifest_f = os.path.expanduser("~/demoplugin.vim")
assert os.environ.get("NVIM_LISTEN_ADDRESS") == nvim_test_addr


class PluginLog(object):

    """Abstract plugin log functionalities"""

    def __init__(self, log_file: str) -> None:
        """Constructor of  PluginLog."""
        self.log_file = log_file

    def match_line(self, line: str) -> bool:
        """Match a str line in the log file."""
        if not line:
            return False

        with open(self.log_file) as f:
            for read_line in f.readlines():
                if line in read_line:
                    return True
        return False


@pytest.fixture(scope="session")
def get_nvim():

    nvim_pid = spawn_nvim(" -u {} ".format(manifest_f))
    assert nvim_pid
    nvim = pynvim.attach("tcp", address="localhost", port=7777)
    assert nvim
    return nvim


def spawn_nvim(cmd: str = ""):
    """Spawn nvim in background."""

    nvim_cmd = "nvim " + cmd + " --headless &"
    print("spawning: " + nvim_cmd)
    popen = subprocess.Popen(
        [nvim_cmd],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        universal_newlines=True,
        shell=True,
    )
    print("spawned: " + nvim_cmd + " pid: " + str(popen.pid))

    time.sleep(0.5)
    assert not popen.returncode
    assert popen.pid
    return popen.pid
