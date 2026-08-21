# User Manual

## 1 Engine

### 1.1 Configure Engine
+ Click **Engine Menu -> Engine Management -> Add Engine**. This opens the file selection dialog. Select the engine file (this program supports loading Chinese chess/Xiangqi engines that use the **UCI** and **UCCI** protocols).
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/1.png?raw=true)
+ Wait one or two seconds for the engine to load successfully. The GUI will automatically display basic information such as the engine path, engine name (if loading multiple engines, it is recommended to set unique names), and protocol. If the engine provides custom configurable options, they will also be displayed in the list on the right. Users can manually modify these engine configurations, but please note that the input characters must be in half-width English format and must not contain special characters or extra spaces.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/2.png?raw=true)
+ Some engines support configurable rules, NNUE weight files, etc., which you can also edit in the list on the right (reloads the engine or restarts the software for changes to take effect). For details regarding custom configurations and values, please refer to the relevant information output by the engine. If you do not know the specific meaning of a configuration, it is recommended to leave it unchanged and keep the default value.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/3.png?raw=true)
+ For example, after loading the Pikafish engine, the default is the Asian Rule (asiazon rule). If you need to configure it to the Chinese Rule, find `Repetition Rule` in the engine configuration options, set it to `ChineseRule`, save, and then restart or reload the engine.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/16.png?raw=true)

### 1.2 Load Engine
+ Select the engine you want to use from the engine list on the main interface, and configure the number of CPU threads and Hash table size.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/4.png?raw=true)

### 1.3 Engine Move Generation / Gameplay
+ The toolbar on the main interface provides functions such as **Engine Plays Black**, **Engine Plays Red**, and **Analysis Mode**. Pressing the corresponding button triggers the interface to invoke the engine for calculation, and the detailed thought process of the engine will be displayed on the right. To cancel the engine move generation, click the button again, and the button will return to its unselected state.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/5.png?raw=true)

### 1.4 Engine Time Settings
+ Click **Engine Menu -> Time Settings** to configure the engine's thinking time and move latency.
+ **Fixed Time**: The engine searches for a fixed duration before making a move, configured in milliseconds (1 second = 1000 milliseconds).
+ **Fixed Depth**: The engine searches to a fixed number of plies/layers before making a move.
+ **Engine Move Latency**: The delay duration before the engine executes a move after finding the search result. This configuration is a time range A-B, where A <= B. The program will randomly pick a time within this range. If configured as 0-0, the latency is disabled.

---

+ **Opening Book Move Latency**: The delay duration before executing a move after finding a match in the opening book. This configuration is a time range A-B, where A <= B. The program will randomly pick a time within this range. If configured as 0-0, the latency is disabled.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/6.png?raw=true)
+ You can also right-click on the chessboard, select **Match Time**, and quickly select a fixed search time for the engine.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/7.png?raw=true)

## 2 Screen Hook / Connection

### 2.1 Using the Connection Feature
+ Click the connection button in the toolbar of the main interface; the mouse cursor will switch to a selection state, then click on the target chessboard you want to hook into. Once the target chessboard is recognized successfully, the connection will start. To cancel the connection, click the connection button again to return it to the unselected state.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/8.png?raw=true)
+ The connection feature on Linux systems relies on `xdotool`. Therefore, ensure this tool is installed before connecting, otherwise the function will be unavailable. Click the connection button, then click the target chessboard, and wait for successful recognition to start. Linux connection does not currently support background mode.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/17.png?raw=true)
+ The operation steps on macOS are similar, but you must grant the relevant system permissions/authorizations, otherwise the connection will fail. When connecting, click the connection button, then click the target chessboard within 3 seconds, and wait for successful recognition to start. macOS connection does not currently support background mode.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/18.png?raw=true)
+ The connection supports two modes: **Auto-Move** and **Spectate**. Select the desired mode before connecting. Auto-Move mode automatically executes moves on the target platform on behalf of our side (make sure it is our turn before connecting), while Spectate mode does not execute moves automatically and only analyzes the position to output detailed engine thinking info.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/9.png?raw=true)

### 2.2 Connection Settings
+ Click **Connection Menu -> Connection Parameters** to open the connection settings dialog box where you can configure the connection routine.
+ **Mouse Click Interval**: Sets the duration interval (from press to release) for left mouse clicks during auto-move, in milliseconds.
+ **Piece Move Interval**: Sets the duration interval between clicking the original position of the piece and clicking the destination position during auto-move, in milliseconds.
+ **Scan Recognition Interval**: Sets how often the connection routine scans the target chessboard, in milliseconds. If your computer performance is high, you can set a smaller value for faster recognition.
+ **Recognition Thread Count**: Sets the number of threads allocated for connection recognition. If computer performance is high, you can set a larger number of threads for faster recognition.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/10.png?raw=true)

### 2.3 Background Mode
+ Click **Connection Menu**, and check **Background Mode**. In this mode, the connection does not occupy your desktop and mouse cursor. The target chessboard can be obscured by other windows, and your mouse can perform other tasks without interference. This is a highly efficient and user-friendly connection mode. The downside is that not all platforms support it (if the connection recognition fails or does not auto-move in this mode, it indicates lack of support).
+ If Background Mode is not supported, uncheck this option so that the connection routine works in Foreground Mode instead. In Foreground Mode, you must ensure the target platform is not obscured by other windows, and the mouse will be occupied during auto-moves. Theoretically, this mode can connect to any platform as long as the target pieces are clear and recognizable.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/11.png?raw=true)

### 2.4 Animation Confirmation
+ If piece movement on the target platform uses transition animations (the piece gradually moves from point A to point B along a path), you need to enable **Animation Confirmation**, otherwise connection failures or recognition errors may occur.
+ If piece movement on the target platform is instant (the piece disappears from point A and appears directly at point B), you can disable **Animation Confirmation**. Disabling it results in higher connection efficiency and faster recognition.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/12.png?raw=true)

## 3 Opening Book

### 3.1 Using the Opening Book
+ Click the **Enable Book Moves** button on the toolbar of the main interface, and the program will prioritize using moves from the opening book. To disable the opening book, click the button again to return it to the unselected state.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/15.png?raw=true)

### 3.2 Opening Book Settings
+ Click **Opening Book Menu -> Book Move Settings** to open the configuration dialog box where you can adjust the opening book move behaviors.
+ **Enable Cloud Book**: The program connects to the cloud book `http://www.chessdb.cn/chessdb.php` to query position data. You can set the timeout value in milliseconds before it automatically disconnects if the cloud book does not respond.
+ **Cloud Endgame Book Only**: Only utilizes endgame position data from the cloud book.
+ **Prioritize Local Book**: If both a local book and the cloud book are configured, the program prioritizes searching positions within the local book.
+ **Book Move Selection Strategy**:
  + **Highest Score**: Selects the move with the highest score evaluation.
  + **Highest Win Rate**: Selects the move with the highest win rate.
  + **Random Positive Score**: Randomly selects among moves with a score greater than 0.
  + **Completely Random**: Randomly selects among all available book moves.
+ **Out-of-Book Move Count**: Sets after how many plies/rounds the program stops using book moves and switches solely to engine calculation.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/13.png?raw=true)

### 3.3 Local Book Management
+ Click **Opening Book Menu -> Local Book Management** to open the local book settings dialog box. Here you can add or delete local books and adjust their priority order (the program searches books from top to bottom).
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/14.png?raw=true)
+ After adding a local book, if you need to modify its path (e.g., changing it to a relative path so the book file can move along with the program without breaking), double-click the path to enter edit mode, modify it, and press Enter to save.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/19.png?raw=true)

## 4 Position / Board State

### 4.1 Edit Position
+ Click **Position Menu** or right-click on the chessboard and select **Edit Position** to open the board editor dialog, allowing you to freely modify the match position. In the editor window, left-click to select or place pieces, right-click to delete pieces, and utilize shortcut buttons at the bottom. Once finished, select **Red First** or **Black First** and click OK.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/20.png?raw=true)
> **Note**: If you set up an illegal or abnormal position and use the engine for calculation, it may cause the engine to crash or exit, depending on the engine's robustness. However, this program imposes no restrictions and allows you to edit positions freely. The same applies to pasting positions described below.

### 4.2 Position FEN
+ In the **Position Menu**, the chessboard right-click context menu, or the toolbar buttons, you can find the **Copy Position FEN** and **Paste Position FEN** options.
+ **Copy Position FEN**: Generates a FEN code based on the current board state and copies it to the system clipboard, allowing you to paste it elsewhere or share it with others.
+ **Paste Position FEN**: Allows you to paste a FEN string copied from elsewhere into the program. The program will automatically initialize the corresponding chess position.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/21.png?raw=true)

### 4.3 Position Image
+ In addition to copying and pasting FEN strings, this program also supports copying and pasting position images. Click **Position Menu** or right-click on the board to see **Copy Position Image** and **Paste Position Image** options.
+ **Copy Position Image**: Generates an image snapshot based on the current board state and copies it to the system clipboard, allowing you to paste it elsewhere or send it to others.
+ **Paste Position Image**: If you copy a chess position image from elsewhere (such as right-clicking and copying an interesting match image from a webpage) and paste it into this program, the GUI will intelligently parse the layout content and generate the corresponding board position, allowing you to analyze it with an engine or play from that state.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/22.png?raw=true)
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/23.png?raw=true)
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/24.png?raw=true)
> **Note**: The intelligent image parsing feature is not 100% successful or accurate. If the image resolution is too low, unclear, or has formatting issues, it may fail to resolve the board state properly. The same applies to importing images described below.

### 4.4 Import and Export
+ Click **Position Menu** or right-click on the chessboard to access the **Import Image to Position** and **Export Position to Image** functions.
+ **Import Image to Position**: Pops up a file selection dialog where you can choose an image file containing a chess layout. The program will intelligently parse the image data to generate the corresponding position. Alternatively, you can drag and drop an image file directly from your file system onto the chessboard to import and parse it.
+ **Export Position to Image**: Pops up a file save dialog where you can choose a directory and name the exported image file. Click confirm to save the current board snapshot to your local drive.
![](https://github.com/sojourners/public-Xiangqi/blob/master/assets/25.png?raw=true)

## 5 Interface Settings

### 5.1 Move Hints
+ Click **Settings Menu -> Enable Move Hints**. When the engine outputs calculation results, hint arrows will be displayed on the chessboard to indicate optimal moves.

### 5.2 Move Sound Effects
+ Click **Settings Menu -> Enable Move Sound Effects**. Audio effects will play when pieces are captured, checked, or moved.

### 5.3 Show Lines / File and Rank Identifiers
+ Click **Settings Menu -> Show Lines**. Checking this option displays numerical file/rank coordinates on the board; unchecking it hides them.

### 5.4 Show Status Bar
+ Click **Settings Menu -> Show Status Bar**. The status bar located underneath the chessboard displays calculation depth, time elapsed, and engine thinking details.

### 5.5 Chessboard Styles / Themes
+ The program offers two theme styles: **Default** and **Red & Black**. The default theme is integrated natively within the program, while the Red & Black theme renders chessboards and pieces using static images. Users can customize their favorite themes by replacing resources under the `./ui` directory.

### 5.6 Chessboard Sizing
+ The program provides multiple chessboard dimensions, including **Large**, **Extra Large**, **Medium**, and **Small**, while also supporting an **Adaptive / Auto-Resize** layout to accommodate various screen configurations.
