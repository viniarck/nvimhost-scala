package nvimhost

// This file was auto generated

import akka.pattern.ask
import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import java.net.InetSocketAddress

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import java.util.concurrent.TimeoutException
import scala.collection.mutable.ArrayBuffer

import com.typesafe.scalalogging.{LazyLogging}

/** Nvim API wrapper. It uses an async TCP socket to connect to Nvim */
class NvimAPI(
    address: InetSocketAddress,
    _timeout: Duration = 1.second
) extends LazyLogging {
  import Conversions._
  import NvimTypes._

  val system = ActorSystem("NvimApi")
  val client = system.actorOf(
    Props(new ClientActor(address, system))
  )

  def bufLineCount(buffer: Buffer): Future[Int] = {

    val msg = Parser.newRequestMsg("nvim_buf_line_count", buffer)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufferGetLine(buffer: Buffer, index: Int): Future[String] = {

    val msg = Parser.newRequestMsg("buffer_get_line", buffer, index)

    val box = StringPromiseBox(Promise[String]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufDetach(buffer: Buffer): Future[Boolean] = {

    val msg = Parser.newRequestMsg("nvim_buf_detach", buffer)

    val box = BooleanPromiseBox(Promise[Boolean]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufferSetLine(buffer: Buffer, index: Int, line: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("buffer_set_line", buffer, index, line)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufferDelLine(buffer: Buffer, index: Int): Future[Unit] = {

    val msg = Parser.newRequestMsg("buffer_del_line", buffer, index)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufferGetLineSlice(
      buffer: Buffer,
      start: Int,
      end: Int,
      include_start: Boolean,
      include_end: Boolean
  ): Future[ArrayBuffer[String]] = {

    val msg = Parser.newRequestMsg(
      "buffer_get_line_slice",
      buffer,
      start,
      end,
      include_start,
      include_end
    )

    val box = ArrayBufferStringPromiseBox(Promise[ArrayBuffer[String]]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufGetLines(
      buffer: Buffer,
      start: Int,
      end: Int,
      strict_indexing: Boolean
  ): Future[ArrayBuffer[String]] = {

    val msg = Parser.newRequestMsg(
      "nvim_buf_get_lines",
      buffer,
      start,
      end,
      strict_indexing
    )

    val box = ArrayBufferStringPromiseBox(Promise[ArrayBuffer[String]]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufferSetLineSlice(
      buffer: Buffer,
      start: Int,
      end: Int,
      include_start: Boolean,
      include_end: Boolean,
      replacement: ArrayBuffer[String]
  ): Future[Unit] = {

    val msg = Parser.newRequestMsg(
      "buffer_set_line_slice",
      buffer,
      start,
      end,
      include_start,
      include_end,
      replacement
    )

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufSetLines(
      buffer: Buffer,
      start: Int,
      end: Int,
      strict_indexing: Boolean,
      replacement: ArrayBuffer[String]
  ): Future[Unit] = {

    val msg = Parser.newRequestMsg(
      "nvim_buf_set_lines",
      buffer,
      start,
      end,
      strict_indexing,
      replacement
    )

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufGetOffset(buffer: Buffer, index: Int): Future[Int] = {

    val msg = Parser.newRequestMsg("nvim_buf_get_offset", buffer, index)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufGetChangedtick(buffer: Buffer): Future[Int] = {

    val msg = Parser.newRequestMsg("nvim_buf_get_changedtick", buffer)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufDelVar(buffer: Buffer, name: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_buf_del_var", buffer, name)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufGetNumber(buffer: Buffer): Future[Int] = {

    val msg = Parser.newRequestMsg("nvim_buf_get_number", buffer)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufGetName(buffer: Buffer): Future[String] = {

    val msg = Parser.newRequestMsg("nvim_buf_get_name", buffer)

    val box = StringPromiseBox(Promise[String]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufSetName(buffer: Buffer, name: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_buf_set_name", buffer, name)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufIsLoaded(buffer: Buffer): Future[Boolean] = {

    val msg = Parser.newRequestMsg("nvim_buf_is_loaded", buffer)

    val box = BooleanPromiseBox(Promise[Boolean]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufIsValid(buffer: Buffer): Future[Boolean] = {

    val msg = Parser.newRequestMsg("nvim_buf_is_valid", buffer)

    val box = BooleanPromiseBox(Promise[Boolean]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufferInsert(
      buffer: Buffer,
      lnum: Int,
      lines: ArrayBuffer[String]
  ): Future[Unit] = {

    val msg = Parser.newRequestMsg("buffer_insert", buffer, lnum, lines)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufGetMark(buffer: Buffer, name: String): Future[ArrayBuffer[Int]] = {

    val msg = Parser.newRequestMsg("nvim_buf_get_mark", buffer, name)

    val box = ArrayBufferIntPromiseBox(Promise[ArrayBuffer[Int]]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufAddHighlight(
      buffer: Buffer,
      ns_id: Int,
      hl_group: String,
      line: Int,
      col_start: Int,
      col_end: Int
  ): Future[Int] = {

    val msg = Parser.newRequestMsg(
      "nvim_buf_add_highlight",
      buffer,
      ns_id,
      hl_group,
      line,
      col_start,
      col_end
    )

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufClearNamespace(
      buffer: Buffer,
      ns_id: Int,
      line_start: Int,
      line_end: Int
  ): Future[Unit] = {

    val msg = Parser.newRequestMsg(
      "nvim_buf_clear_namespace",
      buffer,
      ns_id,
      line_start,
      line_end
    )

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufClearHighlight(
      buffer: Buffer,
      ns_id: Int,
      line_start: Int,
      line_end: Int
  ): Future[Unit] = {

    val msg = Parser.newRequestMsg(
      "nvim_buf_clear_highlight",
      buffer,
      ns_id,
      line_start,
      line_end
    )

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def tabpageListWins(tabpage: TabPage): Future[ArrayBuffer[Window]] = {

    val msg = Parser.newRequestMsg("nvim_tabpage_list_wins", tabpage)

    val box = ArrayBufferWindowPromiseBox(Promise[ArrayBuffer[Window]]())

    client ! Request(msg, box)
    box.p.future

  }

  def tabpageDelVar(tabpage: TabPage, name: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_tabpage_del_var", tabpage, name)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def tabpageGetWin(tabpage: TabPage): Future[Window] = {

    val msg = Parser.newRequestMsg("nvim_tabpage_get_win", tabpage)

    val box = WindowPromiseBox(Promise[Window]())

    client ! Request(msg, box)
    box.p.future

  }

  def tabpageGetNumber(tabpage: TabPage): Future[Int] = {

    val msg = Parser.newRequestMsg("nvim_tabpage_get_number", tabpage)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def tabpageIsValid(tabpage: TabPage): Future[Boolean] = {

    val msg = Parser.newRequestMsg("nvim_tabpage_is_valid", tabpage)

    val box = BooleanPromiseBox(Promise[Boolean]())

    client ! Request(msg, box)
    box.p.future

  }

  def uiAttach(width: Int, height: Int, enable_rgb: Boolean): Future[Unit] = {

    val msg = Parser.newRequestMsg("ui_attach", width, height, enable_rgb)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def uiDetach(): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_ui_detach")

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def uiTryResize(width: Int, height: Int): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_ui_try_resize", width, height)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def command(command: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_command", command)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def feedkeys(
      keys: String,
      mode: String,
      escape_csi: Boolean
  ): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_feedkeys", keys, mode, escape_csi)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def input(keys: String): Future[Int] = {

    val msg = Parser.newRequestMsg("nvim_input", keys)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def replaceTermcodes(
      str: String,
      from_part: Boolean,
      do_lt: Boolean,
      special: Boolean
  ): Future[String] = {

    val msg = Parser.newRequestMsg(
      "nvim_replace_termcodes",
      str,
      from_part,
      do_lt,
      special
    )

    val box = StringPromiseBox(Promise[String]())

    client ! Request(msg, box)
    box.p.future

  }

  def commandOutput(command: String): Future[String] = {

    val msg = Parser.newRequestMsg("nvim_command_output", command)

    val box = StringPromiseBox(Promise[String]())

    client ! Request(msg, box)
    box.p.future

  }

  def strwidth(text: String): Future[Int] = {

    val msg = Parser.newRequestMsg("nvim_strwidth", text)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def listRuntimePaths(): Future[ArrayBuffer[String]] = {

    val msg = Parser.newRequestMsg("nvim_list_runtime_paths")

    val box = ArrayBufferStringPromiseBox(Promise[ArrayBuffer[String]]())

    client ! Request(msg, box)
    box.p.future

  }

  def setCurrentDir(dir: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_set_current_dir", dir)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def getCurrentLine(): Future[String] = {

    val msg = Parser.newRequestMsg("nvim_get_current_line")

    val box = StringPromiseBox(Promise[String]())

    client ! Request(msg, box)
    box.p.future

  }

  def setCurrentLine(line: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_set_current_line", line)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def delCurrentLine(): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_del_current_line")

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def delVar(name: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_del_var", name)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def outWrite(str: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_out_write", str)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def errWrite(str: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_err_write", str)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def errWriteln(str: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_err_writeln", str)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def listBufs(): Future[ArrayBuffer[Buffer]] = {

    val msg = Parser.newRequestMsg("nvim_list_bufs")

    val box = ArrayBufferBufferPromiseBox(Promise[ArrayBuffer[Buffer]]())

    client ! Request(msg, box)
    box.p.future

  }

  def getCurrentBuf(): Future[Buffer] = {

    val msg = Parser.newRequestMsg("nvim_get_current_buf")

    val box = BufferPromiseBox(Promise[Buffer]())

    client ! Request(msg, box)
    box.p.future

  }

  def setCurrentBuf(buffer: Buffer): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_set_current_buf", buffer)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def listWins(): Future[ArrayBuffer[Window]] = {

    val msg = Parser.newRequestMsg("nvim_list_wins")

    val box = ArrayBufferWindowPromiseBox(Promise[ArrayBuffer[Window]]())

    client ! Request(msg, box)
    box.p.future

  }

  def getCurrentWin(): Future[Window] = {

    val msg = Parser.newRequestMsg("nvim_get_current_win")

    val box = WindowPromiseBox(Promise[Window]())

    client ! Request(msg, box)
    box.p.future

  }

  def setCurrentWin(window: Window): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_set_current_win", window)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def listTabpages(): Future[ArrayBuffer[TabPage]] = {

    val msg = Parser.newRequestMsg("nvim_list_tabpages")

    val box = ArrayBufferTabPagePromiseBox(Promise[ArrayBuffer[TabPage]]())

    client ! Request(msg, box)
    box.p.future

  }

  def getCurrentTabpage(): Future[TabPage] = {

    val msg = Parser.newRequestMsg("nvim_get_current_tabpage")

    val box = TabPagePromiseBox(Promise[TabPage]())

    client ! Request(msg, box)
    box.p.future

  }

  def setCurrentTabpage(tabpage: TabPage): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_set_current_tabpage", tabpage)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def createNamespace(name: String): Future[Int] = {

    val msg = Parser.newRequestMsg("nvim_create_namespace", name)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def subscribe(event: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_subscribe", event)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def unsubscribe(event: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_unsubscribe", event)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def getColorByName(name: String): Future[Int] = {

    val msg = Parser.newRequestMsg("nvim_get_color_by_name", name)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def winGetBuf(window: Window): Future[Buffer] = {

    val msg = Parser.newRequestMsg("nvim_win_get_buf", window)

    val box = BufferPromiseBox(Promise[Buffer]())

    client ! Request(msg, box)
    box.p.future

  }

  def winSetBuf(window: Window, buffer: Buffer): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_win_set_buf", window, buffer)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def winGetCursor(window: Window): Future[ArrayBuffer[Int]] = {

    val msg = Parser.newRequestMsg("nvim_win_get_cursor", window)

    val box = ArrayBufferIntPromiseBox(Promise[ArrayBuffer[Int]]())

    client ! Request(msg, box)
    box.p.future

  }

  def winSetCursor(window: Window, pos: ArrayBuffer[Int]): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_win_set_cursor", window, pos)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def winGetHeight(window: Window): Future[Int] = {

    val msg = Parser.newRequestMsg("nvim_win_get_height", window)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def winSetHeight(window: Window, height: Int): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_win_set_height", window, height)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def winGetWidth(window: Window): Future[Int] = {

    val msg = Parser.newRequestMsg("nvim_win_get_width", window)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def winSetWidth(window: Window, width: Int): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_win_set_width", window, width)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def winDelVar(window: Window, name: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("nvim_win_del_var", window, name)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def winGetPosition(window: Window): Future[ArrayBuffer[Int]] = {

    val msg = Parser.newRequestMsg("nvim_win_get_position", window)

    val box = ArrayBufferIntPromiseBox(Promise[ArrayBuffer[Int]]())

    client ! Request(msg, box)
    box.p.future

  }

  def winGetTabpage(window: Window): Future[TabPage] = {

    val msg = Parser.newRequestMsg("nvim_win_get_tabpage", window)

    val box = TabPagePromiseBox(Promise[TabPage]())

    client ! Request(msg, box)
    box.p.future

  }

  def winGetNumber(window: Window): Future[Int] = {

    val msg = Parser.newRequestMsg("nvim_win_get_number", window)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def winIsValid(window: Window): Future[Boolean] = {

    val msg = Parser.newRequestMsg("nvim_win_is_valid", window)

    val box = BooleanPromiseBox(Promise[Boolean]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufferLineCount(buffer: Buffer): Future[Int] = {

    val msg = Parser.newRequestMsg("buffer_line_count", buffer)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufferGetLines(
      buffer: Buffer,
      start: Int,
      end: Int,
      strict_indexing: Boolean
  ): Future[ArrayBuffer[String]] = {

    val msg = Parser.newRequestMsg(
      "buffer_get_lines",
      buffer,
      start,
      end,
      strict_indexing
    )

    val box = ArrayBufferStringPromiseBox(Promise[ArrayBuffer[String]]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufferSetLines(
      buffer: Buffer,
      start: Int,
      end: Int,
      strict_indexing: Boolean,
      replacement: ArrayBuffer[String]
  ): Future[Unit] = {

    val msg = Parser.newRequestMsg(
      "buffer_set_lines",
      buffer,
      start,
      end,
      strict_indexing,
      replacement
    )

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufferGetNumber(buffer: Buffer): Future[Int] = {

    val msg = Parser.newRequestMsg("buffer_get_number", buffer)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufferGetName(buffer: Buffer): Future[String] = {

    val msg = Parser.newRequestMsg("buffer_get_name", buffer)

    val box = StringPromiseBox(Promise[String]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufferSetName(buffer: Buffer, name: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("buffer_set_name", buffer, name)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufferIsValid(buffer: Buffer): Future[Boolean] = {

    val msg = Parser.newRequestMsg("buffer_is_valid", buffer)

    val box = BooleanPromiseBox(Promise[Boolean]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufferGetMark(buffer: Buffer, name: String): Future[ArrayBuffer[Int]] = {

    val msg = Parser.newRequestMsg("buffer_get_mark", buffer, name)

    val box = ArrayBufferIntPromiseBox(Promise[ArrayBuffer[Int]]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufferAddHighlight(
      buffer: Buffer,
      ns_id: Int,
      hl_group: String,
      line: Int,
      col_start: Int,
      col_end: Int
  ): Future[Int] = {

    val msg = Parser.newRequestMsg(
      "buffer_add_highlight",
      buffer,
      ns_id,
      hl_group,
      line,
      col_start,
      col_end
    )

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def bufferClearHighlight(
      buffer: Buffer,
      ns_id: Int,
      line_start: Int,
      line_end: Int
  ): Future[Unit] = {

    val msg = Parser.newRequestMsg(
      "buffer_clear_highlight",
      buffer,
      ns_id,
      line_start,
      line_end
    )

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def tabpageGetWindows(tabpage: TabPage): Future[ArrayBuffer[Window]] = {

    val msg = Parser.newRequestMsg("tabpage_get_windows", tabpage)

    val box = ArrayBufferWindowPromiseBox(Promise[ArrayBuffer[Window]]())

    client ! Request(msg, box)
    box.p.future

  }

  def tabpageGetWindow(tabpage: TabPage): Future[Window] = {

    val msg = Parser.newRequestMsg("tabpage_get_window", tabpage)

    val box = WindowPromiseBox(Promise[Window]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimCommand(command: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("vim_command", command)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimFeedkeys(
      keys: String,
      mode: String,
      escape_csi: Boolean
  ): Future[Unit] = {

    val msg = Parser.newRequestMsg("vim_feedkeys", keys, mode, escape_csi)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimInput(keys: String): Future[Int] = {

    val msg = Parser.newRequestMsg("vim_input", keys)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimReplaceTermcodes(
      str: String,
      from_part: Boolean,
      do_lt: Boolean,
      special: Boolean
  ): Future[String] = {

    val msg = Parser.newRequestMsg(
      "vim_replace_termcodes",
      str,
      from_part,
      do_lt,
      special
    )

    val box = StringPromiseBox(Promise[String]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimCommandOutput(command: String): Future[String] = {

    val msg = Parser.newRequestMsg("vim_command_output", command)

    val box = StringPromiseBox(Promise[String]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimStrwidth(text: String): Future[Int] = {

    val msg = Parser.newRequestMsg("vim_strwidth", text)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimListRuntimePaths(): Future[ArrayBuffer[String]] = {

    val msg = Parser.newRequestMsg("vim_list_runtime_paths")

    val box = ArrayBufferStringPromiseBox(Promise[ArrayBuffer[String]]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimChangeDirectory(dir: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("vim_change_directory", dir)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimGetCurrentLine(): Future[String] = {

    val msg = Parser.newRequestMsg("vim_get_current_line")

    val box = StringPromiseBox(Promise[String]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimSetCurrentLine(line: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("vim_set_current_line", line)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimDelCurrentLine(): Future[Unit] = {

    val msg = Parser.newRequestMsg("vim_del_current_line")

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimOutWrite(str: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("vim_out_write", str)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimErrWrite(str: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("vim_err_write", str)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimReportError(str: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("vim_report_error", str)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimGetBuffers(): Future[ArrayBuffer[Buffer]] = {

    val msg = Parser.newRequestMsg("vim_get_buffers")

    val box = ArrayBufferBufferPromiseBox(Promise[ArrayBuffer[Buffer]]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimGetCurrentBuffer(): Future[Buffer] = {

    val msg = Parser.newRequestMsg("vim_get_current_buffer")

    val box = BufferPromiseBox(Promise[Buffer]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimSetCurrentBuffer(buffer: Buffer): Future[Unit] = {

    val msg = Parser.newRequestMsg("vim_set_current_buffer", buffer)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimGetWindows(): Future[ArrayBuffer[Window]] = {

    val msg = Parser.newRequestMsg("vim_get_windows")

    val box = ArrayBufferWindowPromiseBox(Promise[ArrayBuffer[Window]]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimGetCurrentWindow(): Future[Window] = {

    val msg = Parser.newRequestMsg("vim_get_current_window")

    val box = WindowPromiseBox(Promise[Window]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimSetCurrentWindow(window: Window): Future[Unit] = {

    val msg = Parser.newRequestMsg("vim_set_current_window", window)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimGetTabpages(): Future[ArrayBuffer[TabPage]] = {

    val msg = Parser.newRequestMsg("vim_get_tabpages")

    val box = ArrayBufferTabPagePromiseBox(Promise[ArrayBuffer[TabPage]]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimGetCurrentTabpage(): Future[TabPage] = {

    val msg = Parser.newRequestMsg("vim_get_current_tabpage")

    val box = TabPagePromiseBox(Promise[TabPage]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimSetCurrentTabpage(tabpage: TabPage): Future[Unit] = {

    val msg = Parser.newRequestMsg("vim_set_current_tabpage", tabpage)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimSubscribe(event: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("vim_subscribe", event)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimUnsubscribe(event: String): Future[Unit] = {

    val msg = Parser.newRequestMsg("vim_unsubscribe", event)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def vimNameToColor(name: String): Future[Int] = {

    val msg = Parser.newRequestMsg("vim_name_to_color", name)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def windowGetBuffer(window: Window): Future[Buffer] = {

    val msg = Parser.newRequestMsg("window_get_buffer", window)

    val box = BufferPromiseBox(Promise[Buffer]())

    client ! Request(msg, box)
    box.p.future

  }

  def windowGetCursor(window: Window): Future[ArrayBuffer[Int]] = {

    val msg = Parser.newRequestMsg("window_get_cursor", window)

    val box = ArrayBufferIntPromiseBox(Promise[ArrayBuffer[Int]]())

    client ! Request(msg, box)
    box.p.future

  }

  def windowSetCursor(window: Window, pos: ArrayBuffer[Int]): Future[Unit] = {

    val msg = Parser.newRequestMsg("window_set_cursor", window, pos)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def windowGetHeight(window: Window): Future[Int] = {

    val msg = Parser.newRequestMsg("window_get_height", window)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def windowSetHeight(window: Window, height: Int): Future[Unit] = {

    val msg = Parser.newRequestMsg("window_set_height", window, height)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def windowGetWidth(window: Window): Future[Int] = {

    val msg = Parser.newRequestMsg("window_get_width", window)

    val box = IntPromiseBox(Promise[Int]())

    client ! Request(msg, box)
    box.p.future

  }

  def windowSetWidth(window: Window, width: Int): Future[Unit] = {

    val msg = Parser.newRequestMsg("window_set_width", window, width)

    val box = UnitPromiseBox(Promise[Unit]())

    client ! Request(msg, box)
    box.p.future

  }

  def windowGetPosition(window: Window): Future[ArrayBuffer[Int]] = {

    val msg = Parser.newRequestMsg("window_get_position", window)

    val box = ArrayBufferIntPromiseBox(Promise[ArrayBuffer[Int]]())

    client ! Request(msg, box)
    box.p.future

  }

  def windowGetTabpage(window: Window): Future[TabPage] = {

    val msg = Parser.newRequestMsg("window_get_tabpage", window)

    val box = TabPagePromiseBox(Promise[TabPage]())

    client ! Request(msg, box)
    box.p.future

  }

  def windowIsValid(window: Window): Future[Boolean] = {

    val msg = Parser.newRequestMsg("window_is_valid", window)

    val box = BooleanPromiseBox(Promise[Boolean]())

    client ! Request(msg, box)
    box.p.future

  }

}
