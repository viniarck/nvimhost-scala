#!/usr/bin/env python
# -*- coding: utf-8 -*-

import subprocess
import pytest
import os
import time
from nvim_fixtures import spawn_nvim, PluginLog


@pytest.fixture(scope="session")
def spawn_demoplugin() -> int:
    """Force the bootstrap of the plugin for testing purposes."""

    spawn_nvim()

    cmd = "scala " + os.path.expanduser("~/demoplugin.jar")
    print("spawning: ", cmd)
    popen = subprocess.Popen(
        [cmd],
        shell=True,
        universal_newlines=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
    )

    time.sleep(10)
    assert not popen.returncode
    plugin_pid = popen.pid
    return plugin_pid


class TestDemoPluginBootstrap(object):

    """TestDemoPlugin.
    This test suite forces the bootstrap of the plugin
    by directly calling the plugin binary.
    """

    def test_is_running(self, spawn_demoplugin):
        pid = spawn_demoplugin
        assert pid

        f_log = os.environ.get("NVIM_SCALA_LOG_FILE")
        assert os.path.exists(f_log)

    def test_manifest_exists(self, spawn_demoplugin):
        """Make sure the plugin is generating the manifest vim file."""
        manifest_file = "~/demoplugin.vim"
        assert os.path.exists(os.path.expanduser(manifest_file))

    def test_started(self, spawn_demoplugin):
        """Check if the plugin properly started."""
        f_log = os.environ.get("NVIM_SCALA_LOG_FILE")
        pl = PluginLog(f_log)
        lines = [
            "Validating plugin name",
            "Bootstraping client connection",
            "Bootstraping plugin socket",
            "Successfully connected to localhost",
            "Successfully set the channel "
        ]
        for line in lines:
            assert pl.match_line(line)

    def test_kill(self, spawn_demoplugin):
        plugin_pid = spawn_demoplugin
        assert plugin_pid

        nvim_out = subprocess.check_output(
            ["ps -aux"], shell=True, universal_newlines=True
        )
        for line in nvim_out.split("\n"):
            if "nvim --headless" in line:
                col = line.split()
                nvim_pid = str(col[1]).strip()
                print("found nvim --headless, pid ", nvim_pid)
                out = subprocess.check_output(
                    ["kill " + str(nvim_pid)],
                    shell=True,
                    stderr=subprocess.STDOUT,
                    universal_newlines=True,
                )
                assert not out

        f_log = os.environ.get("NVIM_SCALA_LOG_FILE") or "/tmp/empty.txt"
        os.remove(f_log)
        assert not os.path.exists(f_log)
        assert not out
