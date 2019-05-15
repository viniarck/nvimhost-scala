#!/usr/bin/python
# -*- coding: utf-8 -*-
# forked from https://github.com/DaikiMaekawa/neovim.cpp/blob/master/gen_api.py

from jinja2 import Environment, FileSystemLoader
import msgpack
import os
import subprocess
import re
from typing import Dict, Any


class InvalidType(Exception):
    pass


class NativeType:
    def __init__(self, name, regex=""):
        self.name = name
        self.container = False
        self.regex = regex


t_types = {
    "Boolean": NativeType("Boolean"),
    "String": NativeType("String"),
    "void": NativeType("Unit"),
    "Integer": NativeType("Int"),
    "Window": NativeType("Window"),
    "Buffer": NativeType("Buffer"),
    "Tabpage": NativeType("TabPage"),
}

# For now Dicts and Objects excluded. Most of the core functions have been covered.
excluded_types = {"Dictionary": "", "Object": "", "Array": ""}
excluded_methods = {"get_api_info": "", "vim_get_api_info": ""}

ext_types = ["Buffer", "Window", "Tabpage"]
derived_types = {"ArrayOf": NativeType("T[]", r"\w+\((\w+)(.*?(\d+))?\)")}


def type_to_native(str_t):
    if str_t in t_types:
        return t_types[str_t].name
    for key in derived_types.keys():
        if key in str_t:
            t = derived_types[key]
            m = re.match(t.regex, str_t)
            if m:
                return "ArrayBuffer[{}]".format(type_to_native(m.group(1)))
    raise InvalidType({"type": str_t})


def gen_tmpl_arg(ret_type):
    tmpl_val = ret_type
    # nested substring matching
    for item in ext_types:
        if item in ret_type:
            tmpl_val = tmpl_val.replace(item, "ExtValue")
    return tmpl_val


def snake_to_camel(str_input):
    res = ""
    words = str_input.split("_")
    res = words[0]
    for word in words[1:]:
        res += word.capitalize()
    return res


def gen_func(f_name, name, return_type, tmpl_arg, is_async, args):
    return {
        "name": name,
        "f_name": f_name,
        "return": return_type,
        "tmpl_arg": tmpl_arg,
        "is_async": is_async,
        "args": args,
    }


def gen_api(pre_print=False, post_print=False, gen_file=True):
    file_name = "Api.scala"
    source_dir = os.path.join("src", "main", "scala", "nvimhost")
    env = Environment(loader=FileSystemLoader("templates", encoding="utf8"))
    tpl = env.get_template(file_name.split(".")[0].lower() + ".j2")

    api_info = subprocess.check_output(["nvim", "--api-info"])
    unpacked_api = msgpack.unpackb(api_info, raw=False)

    functions: Dict[str, Any] = {}
    for f in unpacked_api["functions"]:
        name = f.get("name", "")
        if name in excluded_methods:
            continue
        f_name = snake_to_camel(name.replace("nvim_", ""))
        if f_name in functions:
            continue
        try:
            if pre_print:
                print(f, "\n")
            is_async = False
            return_type = type_to_native(f.get("return_type", ""))
            tmpl_arg = gen_tmpl_arg(return_type)
            args = [
                {"type": type_to_native(arg[0]), "name": arg[1]}
                for arg in f["parameters"]
            ]
            parsed_funcs: Dict[str, Any] = {}
            func_def = gen_func(f_name, name, return_type, tmpl_arg, is_async, args)
            parsed_funcs[f_name] = func_def
            # Ignored for now since all functions are wrapped in a Future
            if return_type == "void":
                f_name = f_name + "Async"
                is_async = True
                func_def = gen_func(f_name, name, return_type, tmpl_arg, is_async, args)
                parsed_funcs[f_name] = func_def
            if post_print:
                [print(f, "\n") for k, f in parsed_funcs.items()]
            functions.update(parsed_funcs)
        except InvalidType as e:
            if e.args[0]["type"] in excluded_types:
                pass
            else:
                print("Invalid func = " + str(f))

    api = tpl.render({"functions": list(functions.values())})

    if gen_file:
        if not os.path.isdir(source_dir):
            os.makedirs(source_dir)
        with open(os.path.join(source_dir, file_name), "w+") as f:
            f.write(api)


def main():
    gen_api(pre_print=True, post_print=True, gen_file=True)


if __name__ == "__main__":
    main()
