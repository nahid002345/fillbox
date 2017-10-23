package com.nahid.diordna.fillbox;

import java.util.Collection;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * UI stuff.
 * 
 */
public class MainActivity extends Activity {
	// instance variables
	private Game game;
	private int boardSize;
	private Typeface fontAwesome;
	private GridView gridView;
	private TextView whiteCount;
	private TextView blackCount;
	private TextView msgbox;

	private ImageView sample;

	private MenuItem passItem;

	private int touchDirection = 0;

	private int blueFill = 0;
	private int redFill = 0;

	private String msg;
	// constants
	private static final String GAME_KEY = "game";
	private static final String BOARD_SIZE_KEY = "board_size";

	// Rules constants. used for direction
	public static final int DIRECTION_LEFT = 0;
	public static final int DIRECTION_RIGHT = 1;
	public static final int DIRECTION_TOP = 2;
	public static final int DIRECTION_BOTTOM = 3;
	public static final int DIRECTION_TOPRIGHT = 4;
	public static final int DIRECTION_TOPLEFT = 5;
	public static final int DIRECTION_TOPBOTTOM = 6;
	public static final int DIRECTION_DOWNRIGHT = 7;
	public static final int DIRECTION_DOWNLEFT = 8;
	public static final int DIRECTION_RIGHTLEFT = 9;
	public static final int DIRECTION_TOPRIGHTLEFT = 10;
	public static final int DIRECTION_BOTTOMRIGHTLEFT = 11;
	public static final int DIRECTION_TOPRIGHTBOTTOM = 12;
	public static final int DIRECTION_TOPLEFTBOTTOM = 13;

	public static final int DIRECTION_FULL = 100;

	public static final int FULL_TR = 0;
	public static final int FULL_TL = 1;
	public static final int FULL_BL = 2;
	public static final int FULL_BR = 3;
	public static final int FULL_TRTL = 4;
	public static final int FULL_TRBL = 5;
	public static final int FULL_TRBR = 6;
	public static final int FULL_TLBL = 7;
	public static final int FULL_TLBR = 8;
	public static final int FULL_BLBR = 9;
	public static final int FULL_TRTLBL = 10;
	public static final int FULL_TRTLBR = 11;
	public static final int FULL_TRBLBR = 12;
	public static final int FULL_TLBLBR = 13;
	public static final int FULL_TRTLBLBR = 14;

	public boolean gridDirectionInfo[][];
	public boolean pointFillInfo[][];
	public boolean blueFillInfo[][];
	public boolean redFillInfo[][];
	public static boolean PlayerMove = false;
	public boolean changePlayerDecision = true;
	public static boolean RedBluePlayer;
	public String opponentPlayer;
	public static boolean playerType = false;
	public static boolean dialogLog = false;

	/**
	 * Sets up board, buttons, menus, etc.
	 */

	private void initializeGame() {
		int boardSize_Direction = game.getBoardSize();
		gridDirectionInfo = new boolean[boardSize_Direction
				* boardSize_Direction][4];

		pointFillInfo = new boolean[boardSize_Direction * boardSize_Direction][4];

		blueFillInfo = new boolean[boardSize_Direction * boardSize_Direction][4];
		redFillInfo = new boolean[boardSize_Direction * boardSize_Direction][4];
		blueFill = 0;
		redFill = 0;
		RedBluePlayer = PlayerMove;
		if (RedBluePlayer)
			opponentPlayer = "Blue";
		else
			opponentPlayer = "Red";
		// if(playerType) opponentPlayer="";

	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		setContentView(R.layout.activity_main);
		whiteCount = (TextView) findViewById(R.id.white_taken);
		blackCount = (TextView) findViewById(R.id.black_taken);
		msgbox = (TextView) findViewById(R.id.message);
		gridView = (GridView) findViewById(R.id.gridview);
		sample = (ImageView) findViewById(R.id.black);
		//
		// AdView adView;
		// adView = new AdView(this, AdSize.BANNER, "a14eb6c98335a35");
		// LinearLayout l=(LinearLayout)findViewById(R.id.linearlayout);
		// l.addView(adView);
		//
		// AdRequest request = new AdRequest();
		// adView.loadAd(request);

		fontAwesome = Typefaces.get(this, "fonts/fontawesome-webfont.ttf");

		if (savedInstanceState == null) {
			game = newGameFromSettings();
		} else {
			game = savedInstanceState.getParcelable(GAME_KEY);
			boardSize = savedInstanceState.getInt(BOARD_SIZE_KEY);
		}

		initializeGame();
		refreshCaptured();
		setupBoard();

	}

	private ImageAdapter setupBoard() {
		ImageAdapter adapter = new ImageAdapter(this);
		gridView.setNumColumns(boardSize);
		gridView.setAdapter(adapter);
		return adapter;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(GAME_KEY, game);
		outState.putInt(BOARD_SIZE_KEY, boardSize);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		game = savedInstanceState.getParcelable(GAME_KEY);
		boardSize = savedInstanceState.getInt(BOARD_SIZE_KEY);
	}

	// Accessor methods

	/**
	 * Get the <code>Game</code> object currently referenced by this UI.
	 * 
	 * @return Current <code>Game</code>
	 */
	protected Game getGame() {
		return game;
	}

	/**
	 * Return the current game's board size.
	 * 
	 * @return Number of vertical or horizontal lines. For example, a 19x19
	 *         board will return 19.
	 */
	protected int getBoardSize() {
		return boardSize;
	}

	// other methods

	/**
	 * Update the <code>TextView</code>s displaying the number of captured
	 * stones.
	 */
	protected void showToast() {
		String player;
		ImageView blue= (ImageView)findViewById(R.id.white);
		ImageView red= (ImageView)findViewById(R.id.black);
		if (RedBluePlayer)
			player = "Red";
		else
			player = "Blue";
		final ProgressDialog ringProgressDialog = ProgressDialog.show(
				MainActivity.this, player + "'s Move", "Please Wait ...", true);
		ringProgressDialog.setCancelable(true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {

				}
				ringProgressDialog.dismiss();
			}
		}).start();
		if(RedBluePlayer){
			blue.setImageResource(R.drawable.go_white_no_bg_inactive);
			red.setImageResource(R.drawable.go_black_no_bg);
		}
		else{
			blue.setImageResource(R.drawable.go_white_no_bg);
			red.setImageResource(R.drawable.go_black_no_bg_inactive);
		}
	}

	protected void refreshCaptured() {

		whiteCount.setText(String.valueOf(blueFill));
		blackCount.setText(String.valueOf(redFill));
		final Context context = this;
		msgbox.setText(changePlayerMessage());
		if ((blueFill + redFill) == ((boardSize - 1) * (boardSize - 1))) {
			msgbox.setText("Game Over");
			dialogLog = true;
			final Dialog dialog = new Dialog(context);
			dialog.setContentView(R.layout.game_over_dialog);
			dialog.setTitle("Game Over");

			// set the custom dialog components - text, image and button
			TextView text = (TextView) dialog.findViewById(R.id.text);

			ImageView image = (ImageView) dialog.findViewById(R.id.image);
			Button dialogButton = (Button) dialog
					.findViewById(R.id.dialogButtonOK);
			if (blueFill > redFill) {
				image.setImageResource(R.drawable.go_white_no_bg_game_over);
				text.setText("Player Blue Wins !!!! :)");
			} else {
				image.setImageResource(R.drawable.go_black_no_bg_game_over);
				text.setText("Player Red Wins !!!! :)");
			}

			// if button is clicked, close the custom dialog
			dialogButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();
		}
		// else
		// showToast();
		if (playerType)
			showToast();

	}

	private String changePlayerMessage() {
		if (RedBluePlayer)
			return "Red's Move Now";
		else
			return "Blue's Move Now";
	}

	private Game newGameFromSettings() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		int koRule = prefs.getString("ko", "0").equals("0") ? Game.SITUATIONAL
				: Game.POSITIONAL;
		boolean suicideRule = prefs.getString("suicide", "0").equals("1") ? true
				: false;
		boardSize = Integer.parseInt(prefs.getString("board_size", "10"));

		return new Game(koRule, suicideRule, boardSize);
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(
				MainActivity.this);
		alertbox.setTitle("Warning");
		// alertbox.setIcon(R.drawable.info);
		alertbox.setMessage("Do you want to quit game?");
		alertbox.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
		alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.cancel();
			}
		});
		alertbox.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//
		// passItem = menu
		// .add(R.string.pass_turn)
		// .setShowAsActionFlags(
		// MenuItem.SHOW_AS_ACTION_ALWAYS
		// | MenuItem.SHOW_AS_ACTION_WITH_TEXT)
		// .setOnMenuItemClickListener(
		// new MenuItem.OnMenuItemClickListener() {
		// @Override
		// public boolean onMenuItemClick(MenuItem item) {
		// game.passTurn();
		// refreshPassItem();
		// return true;
		// }
		// });
		// ;

		SubMenu subMenu1 = menu.addSubMenu("Action Item");
		subMenu1.add(R.string.new_game)
				.setShowAsActionFlags(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT)
				.setOnMenuItemClickListener(
						new MenuItem.OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem item) {
								AlertDialog.Builder alertbox = new AlertDialog.Builder(
										MainActivity.this);
								alertbox.setTitle("Warning");
								// alertbox.setIcon(R.drawable.info);
								alertbox.setMessage("Do you want a new game to play?");
								alertbox.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface arg0, int arg1) {
												game = newGameFromSettings();

												setupBoard();
												// refreshPassItem();
												initializeGame();
												refreshCaptured();
												
											}
										});
								alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0, int arg1) {
										arg0.cancel();
									}
								});
								alertbox.show();
								return true;

								// Intent browserIntent = new
								// Intent("android.intent.action.choosePlayer");
								// startActivity(browserIntent);
								
							}
						});
		subMenu1.add("Settings").setIntent(new Intent(this, Settings.class));
		subMenu1.add("About").setIntent(new Intent(this, AboutActivity.class));

		MenuItem subMenu1Item = subMenu1.getItem();
		subMenu1Item.setIcon(R.drawable.ic_action_settings)
				.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 
	 * Adapter used the update the board graphics.
	 * 
	 */
	public class ImageAdapter extends BaseAdapter {
		private MainActivity mainActivity;
		private int[] mThumbIds;

		public ImageAdapter(Context c) {
			mainActivity = (MainActivity) c;
			char[] position = mainActivity.getGame().getPosition();
			mThumbIds = new int[position.length];

			for (int i = 0; i < position.length; i++) {
				mThumbIds[i] = makeImageResource(i, position[i]);
			}
		}

		public int getCount() {
			return mThumbIds.length;
		}

		public void setImageResource(int index, char color, String direction,
				boolean isFull) {
			mThumbIds[index] = makeImageResource(index, color, direction,
					isFull);
		}

		public Object getItem(int index) {
			return null;
		}

		public long getItemId(int index) {
			return 0;
		}

		@SuppressLint("NewApi")
		public View getView(int index, View convertView, ViewGroup parent) {

			ImageView imageView;
			if (convertView == null) { // if it's not recycled, initialize some
										// attributes
				imageView = new ImageView(mainActivity);
				int height;
				if (Build.VERSION.SDK_INT >= 16) {
					height = ((GridView) parent).getColumnWidth();
				} else {
					height = ((GridView) parent).getWidth()
							/ mainActivity.getBoardSize();
				}
				imageView.setLayoutParams(new GridView.LayoutParams(
						GridView.LayoutParams.MATCH_PARENT, height));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(0, 0, 0, 0);
				imageView.setOnClickListener(new ImageView.OnClickListener() {
					@Override
					public void onClick(View iV) {

						sample.setVisibility(View.VISIBLE);
						sample.setPadding(50, 50, 0, 0);
						// mainActivity.refreshCaptured();
					}
				});
				imageView.setOnTouchListener(new ImageView.OnTouchListener() {

					float preX = 0;
					float preY = 0;

					@Override
					public boolean onTouch(View iV, MotionEvent mEvent) {

						float x = mEvent.getX();
						float y = mEvent.getY();
						float downX, downY, upX, upY;

						switch (mEvent.getAction()) {

						case MotionEvent.ACTION_UP:
							preX = mEvent.getX();
							preY = mEvent.getY();

						case MotionEvent.ACTION_DOWN:
							preX = mEvent.getX();
							preY = mEvent.getY();

						case MotionEvent.ACTION_MOVE: {
							x = mEvent.getX();
							y = mEvent.getY();

							float dx = x - preX;
							float dy = y - preY;

							if ((float) ((Math.abs(dx)) / (Math.abs(dy))) > 1) {

								int indexN = ((GridView) iV.getParent())
										.getPositionForView(iV);
								if (dx > 0) {
									if (indexN % boardSize < boardSize - 1) {

										if (!gridDirectionInfo[indexN][DIRECTION_RIGHT]) {
											gridDirectionInfo[indexN][DIRECTION_RIGHT] = true;
											updateBoard(game.setLine(indexN));

											gridDirectionInfo[indexN + 1][DIRECTION_LEFT] = true;
											updateBoard(game
													.setLine(indexN + 1));

											IsFillBox(indexN, DIRECTION_RIGHT);
										}
									}

								} else {
									if (indexN % boardSize > 0) {
										if (!gridDirectionInfo[indexN][DIRECTION_LEFT]) {
											gridDirectionInfo[indexN][DIRECTION_LEFT] = true;
											updateBoard(game.setLine(indexN));

											gridDirectionInfo[indexN - 1][DIRECTION_RIGHT] = true;
											updateBoard(game
													.setLine(indexN - 1));

											IsFillBox(indexN, DIRECTION_LEFT);
										}
									}
								}
								mainActivity.refreshCaptured();

							} else if (((Math.abs(dx)) / (Math.abs(dy))) < 1) {

								Game game = mainActivity.getGame();
								int indexN = ((GridView) iV.getParent())
										.getPositionForView(iV);
								if (dy < 0) {
									if (indexN + 1 > boardSize) {
										if (!gridDirectionInfo[indexN][DIRECTION_TOP]) {
											gridDirectionInfo[indexN][DIRECTION_TOP] = true;
											updateBoard(game.setLine(indexN));

											gridDirectionInfo[indexN
													- boardSize][DIRECTION_BOTTOM] = true;
											updateBoard(game.setLine(indexN
													- boardSize));

											IsFillBox(indexN, DIRECTION_TOP);
										}
									}

								} else {
									if (indexN < (boardSize * (boardSize - 1))) {

										if (!gridDirectionInfo[indexN][DIRECTION_BOTTOM]) {
											gridDirectionInfo[indexN][DIRECTION_BOTTOM] = true;
											updateBoard(game.setLine(indexN));

											gridDirectionInfo[indexN
													+ boardSize][DIRECTION_TOP] = true;
											updateBoard(game.setLine(indexN
													+ boardSize));

											IsFillBox(indexN, DIRECTION_BOTTOM);
										}
									}

								}
								mainActivity.refreshCaptured();

							}

							break;

						}

						}

						return true;
					}

				});
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setImageResource(mThumbIds[index]);

			return imageView;
		}

		private int makeImageResource(int index, char color) {

			String strColor;
			String drawableName;
			int b = mainActivity.getBoardSize();

			switch (color) {
			case Game.WHITE:
				strColor = "white";
				break;
			case Game.BLACK:
				strColor = "black";
				break;

			case Game.LINE:
				strColor = "line";
				break;
			case Game.EMPTY:
			default:
				strColor = "empty";
				break;
			}

			if (index == 0) {
				// Top-Left
				drawableName = "f_" + strColor + "_tl";
			} else if (index == b - 1) {
				// Top-Right
				drawableName = "f_" + strColor + "_tr";
			} else if (index > 0 && index < b - 1) {
				// Top
				drawableName = "f_" + strColor + "_t";
			} else if (index == b * (b - 1)) {
				// Bottom-Left
				drawableName = "f_" + strColor + "_bl";
			} else if (index == b * b - 1) {
				// Bottom-Right
				drawableName = "f_" + strColor + "_br";
			} else if (index > b * (b - 1) && index < b * b - 1) {
				// Bottom
				drawableName = "f_" + strColor + "_b";
			} else if (index % b == b - 1) {
				// Right
				drawableName = "f_" + strColor + "_r";
			} else if (index % b == 0) {
				// Left
				drawableName = "f_" + strColor + "_l";
			} else {
				// Middle
				drawableName = "f_" + strColor;
			}

			return mainActivity.getResources().getIdentifier(drawableName,
					"drawable", mainActivity.getPackageName());
		}

		private int makeImageResource(int index, char color, String direction,
				boolean isFull) {
			String strColor;
			String drawableName;
			String fillInfo;
			int b = mainActivity.getBoardSize();

			switch (color) {
			case Game.WHITE:
				strColor = "white";
				break;
			case Game.BLACK:
				strColor = "black";
				break;

			case Game.LINE:
				strColor = "line";
				break;
			case Game.EMPTY:
			default:
				strColor = "empty";
				break;
			}

			if (index == 0) {
				// Top-Left
				drawableName = "f_" + strColor + "_tl";
			} else if (index == b - 1) {
				// Top-Right
				drawableName = "f_" + strColor + "_tr";
			} else if (index > 0 && index < b - 1) {
				// Top
				drawableName = "f_" + strColor + "_t";
			} else if (index == b * (b - 1)) {
				// Bottom-Left
				drawableName = "f_" + strColor + "_bl";
			} else if (index == b * b - 1) {
				// Bottom-Right
				drawableName = "f_" + strColor + "_br";
			} else if (index > b * (b - 1) && index < b * b - 1) {
				// Bottom
				drawableName = "f_" + strColor + "_b";
			} else if (index % b == b - 1) {
				// Right
				drawableName = "f_" + strColor + "_r";
			} else if (index % b == 0) {
				// Left
				drawableName = "f_" + strColor + "_l";
			} else {
				// Middle
				drawableName = "f_" + strColor;
			}

			fillInfo = FillBoxInfoRedBlue(index);

			drawableName = drawableName + "_" + direction + fillInfo;
			return mainActivity.getResources().getIdentifier(drawableName,
					"drawable", mainActivity.getPackageName());
		}

		public void updateBoard(Collection<Integer> indices) {

			// Log.i("updateBoard","updateBoard");
			String direction_updateBoard;
			char[] position = mainActivity.getGame().getPosition();
			for (int index : indices) {

				direction_updateBoard = String.valueOf(getDirection(index));
				setImageResource(index, position[index], direction_updateBoard,
						false);
				notifyDataSetChanged();
			}
		}

		public void refreshBoard() {

			String direction_updateBoard;
			char[] position = mainActivity.getGame().getPosition();
			for (int i = 0; i < position.length; i++) {
				direction_updateBoard = String.valueOf(getDirection(i));
				setImageResource(i, position[i], direction_updateBoard, false);
			}
			notifyDataSetChanged();
		}

		public int getDirection(int index) {
			int direction = 0;
			Game game = mainActivity.getGame();
			// Log.i("boolean value",String.valueOf(gridDirectionInfo[index][DIRECTION_LEFT])
			// +
			// String.valueOf(gridDirectionInfo[index][DIRECTION_RIGHT])+String.valueOf(gridDirectionInfo[index][DIRECTION_TOP])+String.valueOf(gridDirectionInfo[index][DIRECTION_BOTTOM])
			// );
			if (gridDirectionInfo[index][DIRECTION_BOTTOM]
					&& gridDirectionInfo[index][DIRECTION_RIGHT]
					&& gridDirectionInfo[index][DIRECTION_LEFT]
					&& gridDirectionInfo[index][DIRECTION_TOP])
				direction = DIRECTION_FULL;
			else if (gridDirectionInfo[index][DIRECTION_BOTTOM]
					&& gridDirectionInfo[index][DIRECTION_RIGHT]
					&& gridDirectionInfo[index][DIRECTION_LEFT])
				direction = DIRECTION_BOTTOMRIGHTLEFT;
			else if (gridDirectionInfo[index][DIRECTION_TOP]
					&& gridDirectionInfo[index][DIRECTION_RIGHT]
					&& gridDirectionInfo[index][DIRECTION_LEFT])
				direction = DIRECTION_TOPRIGHTLEFT;
			else if (gridDirectionInfo[index][DIRECTION_TOP]
					&& gridDirectionInfo[index][DIRECTION_RIGHT]
					&& gridDirectionInfo[index][DIRECTION_BOTTOM])
				direction = DIRECTION_TOPRIGHTBOTTOM;
			else if (gridDirectionInfo[index][DIRECTION_TOP]
					&& gridDirectionInfo[index][DIRECTION_LEFT]
					&& gridDirectionInfo[index][DIRECTION_BOTTOM])
				direction = DIRECTION_TOPLEFTBOTTOM;

			else if (gridDirectionInfo[index][DIRECTION_RIGHT]
					&& gridDirectionInfo[index][DIRECTION_LEFT])
				direction = DIRECTION_RIGHTLEFT;
			else if (gridDirectionInfo[index][DIRECTION_BOTTOM]
					&& gridDirectionInfo[index][DIRECTION_LEFT])
				direction = DIRECTION_DOWNLEFT;
			else if (gridDirectionInfo[index][DIRECTION_BOTTOM]
					&& gridDirectionInfo[index][DIRECTION_RIGHT])
				direction = DIRECTION_DOWNRIGHT;
			else if (gridDirectionInfo[index][DIRECTION_TOP]
					&& gridDirectionInfo[index][DIRECTION_BOTTOM])
				direction = DIRECTION_TOPBOTTOM;
			else if (gridDirectionInfo[index][DIRECTION_TOP]
					&& gridDirectionInfo[index][DIRECTION_LEFT])
				direction = DIRECTION_TOPLEFT;
			else if (gridDirectionInfo[index][DIRECTION_TOP]
					&& gridDirectionInfo[index][DIRECTION_RIGHT])
				direction = DIRECTION_TOPRIGHT;
			else if (gridDirectionInfo[index][DIRECTION_LEFT])
				direction = DIRECTION_LEFT;
			else if (gridDirectionInfo[index][DIRECTION_RIGHT])
				direction = DIRECTION_RIGHT;
			else if (gridDirectionInfo[index][DIRECTION_TOP])
				direction = DIRECTION_TOP;
			else if (gridDirectionInfo[index][DIRECTION_BOTTOM])
				direction = DIRECTION_BOTTOM;

			else
				direction = -1;

			return direction;

		}

		public void IsFillBox(int index, int direction) {
			boolean checkPlayer = true;
			switch (direction) {
			case DIRECTION_LEFT:
				if (index - boardSize >= 0) {
					if (gridDirectionInfo[index][DIRECTION_LEFT]
							&& gridDirectionInfo[index - 1][DIRECTION_RIGHT]
							&& gridDirectionInfo[index - 1][DIRECTION_TOP]
							&& gridDirectionInfo[index - boardSize - 1][DIRECTION_BOTTOM]
							&& gridDirectionInfo[index - boardSize - 1][DIRECTION_RIGHT]
							&& gridDirectionInfo[index - boardSize][DIRECTION_LEFT]
							&& gridDirectionInfo[index - boardSize][DIRECTION_BOTTOM]
							&& gridDirectionInfo[index][DIRECTION_TOP]) {
						pointFillInfo[index][FULL_TL] = true;
						pointFillInfo[index - 1][FULL_TR] = true;
						pointFillInfo[index - boardSize - 1][FULL_BR] = true;
						pointFillInfo[index - boardSize][FULL_BL] = true;

						checkPlayer = false;
						if (RedBluePlayer) {
							redFillInfo[index][FULL_TL] = true;
							redFillInfo[index - 1][FULL_TR] = true;
							redFillInfo[index - boardSize - 1][FULL_BR] = true;
							redFillInfo[index - boardSize][FULL_BL] = true;
							redFill += 1;

						}

						else {
							blueFillInfo[index][FULL_TL] = true;
							blueFillInfo[index - 1][FULL_TR] = true;
							blueFillInfo[index - boardSize - 1][FULL_BR] = true;
							blueFillInfo[index - boardSize][FULL_BL] = true;
							blueFill += 1;
						}

						setImageResource(index, Game.LINE,
								String.valueOf(getDirection(index)), true);
						setImageResource(index - 1, Game.LINE,
								String.valueOf(getDirection(index - 1)), true);
						setImageResource(
								index - boardSize - 1,
								Game.LINE,
								String.valueOf(getDirection(index - boardSize
										- 1)), true);
						setImageResource(
								index - boardSize,
								Game.LINE,
								String.valueOf(getDirection(index - boardSize)),
								true);

						notifyDataSetChanged();

					}
				}

				if (index + boardSize < (boardSize * boardSize)) {
					if (gridDirectionInfo[index][DIRECTION_LEFT]
							&& gridDirectionInfo[index - 1][DIRECTION_RIGHT]
							&& gridDirectionInfo[index - 1][DIRECTION_BOTTOM]
							&& gridDirectionInfo[index + boardSize - 1][DIRECTION_TOP]
							&& gridDirectionInfo[index + boardSize - 1][DIRECTION_RIGHT]
							&& gridDirectionInfo[index + boardSize][DIRECTION_LEFT]
							&& gridDirectionInfo[index + boardSize][DIRECTION_TOP]
							&& gridDirectionInfo[index][DIRECTION_BOTTOM]) {

						pointFillInfo[index][FULL_BL] = true;
						pointFillInfo[index - 1][FULL_BR] = true;
						pointFillInfo[index + boardSize - 1][FULL_TR] = true;
						pointFillInfo[index + boardSize][FULL_TL] = true;

						checkPlayer = false;
						if (RedBluePlayer) {
							redFillInfo[index][FULL_BL] = true;
							redFillInfo[index - 1][FULL_BR] = true;
							redFillInfo[index + boardSize - 1][FULL_TR] = true;
							redFillInfo[index + boardSize][FULL_TL] = true;
							redFill += 1;
						}

						else {
							blueFillInfo[index][FULL_BL] = true;
							blueFillInfo[index - 1][FULL_BR] = true;
							blueFillInfo[index + boardSize - 1][FULL_TR] = true;
							blueFillInfo[index + boardSize][FULL_TL] = true;
							blueFill += 1;
						}

						setImageResource(index, Game.LINE,
								String.valueOf(getDirection(index)), true);
						setImageResource(index - 1, Game.LINE,
								String.valueOf(getDirection(index - 1)), true);
						setImageResource(
								index + boardSize - 1,
								Game.LINE,
								String.valueOf(getDirection(index + boardSize
										- 1)), true);
						setImageResource(
								index + boardSize,
								Game.LINE,
								String.valueOf(getDirection(index + boardSize)),
								true);

						notifyDataSetChanged();

					}
				}
				if (checkPlayer)
					changePlayer();
				else
					checkOpponentPlayer();
				break;

			case DIRECTION_RIGHT:
				if (index - boardSize >= 0) {
					if (gridDirectionInfo[index][DIRECTION_RIGHT]
							&& gridDirectionInfo[index + 1][DIRECTION_LEFT]
							&& gridDirectionInfo[index + 1][DIRECTION_TOP]
							&& gridDirectionInfo[index - boardSize + 1][DIRECTION_BOTTOM]
							&& gridDirectionInfo[index - boardSize + 1][DIRECTION_LEFT]
							&& gridDirectionInfo[index - boardSize][DIRECTION_RIGHT]
							&& gridDirectionInfo[index - boardSize][DIRECTION_BOTTOM]
							&& gridDirectionInfo[index][DIRECTION_TOP]) {
						pointFillInfo[index][FULL_TR] = true;
						pointFillInfo[index + 1][FULL_TL] = true;
						pointFillInfo[index - boardSize + 1][FULL_BL] = true;
						pointFillInfo[index - boardSize][FULL_BR] = true;
						checkPlayer = false;
						if (RedBluePlayer) {
							redFillInfo[index][FULL_TR] = true;
							redFillInfo[index + 1][FULL_TL] = true;
							redFillInfo[index - boardSize + 1][FULL_BL] = true;
							redFillInfo[index - boardSize][FULL_BR] = true;
							redFill += 1;
						}

						else {
							blueFillInfo[index][FULL_TR] = true;
							blueFillInfo[index + 1][FULL_TL] = true;
							blueFillInfo[index - boardSize + 1][FULL_BL] = true;
							blueFillInfo[index - boardSize][FULL_BR] = true;
							blueFill += 1;
						}

						setImageResource(index, Game.LINE,
								String.valueOf(getDirection(index)), true);
						setImageResource(index + 1, Game.LINE,
								String.valueOf(getDirection(index + 1)), true);
						setImageResource(
								index - boardSize + 1,
								Game.LINE,
								String.valueOf(getDirection(index - boardSize
										+ 1)), true);
						setImageResource(
								index - boardSize,
								Game.LINE,
								String.valueOf(getDirection(index - boardSize)),
								true);

						notifyDataSetChanged();

					}
				}

				if (index + boardSize < (boardSize * boardSize)) {
					if (gridDirectionInfo[index][DIRECTION_RIGHT]
							&& gridDirectionInfo[index + 1][DIRECTION_LEFT]
							&& gridDirectionInfo[index + 1][DIRECTION_BOTTOM]
							&& gridDirectionInfo[index + boardSize + 1][DIRECTION_TOP]
							&& gridDirectionInfo[index + boardSize + 1][DIRECTION_LEFT]
							&& gridDirectionInfo[index + boardSize][DIRECTION_RIGHT]
							&& gridDirectionInfo[index + boardSize][DIRECTION_TOP]
							&& gridDirectionInfo[index][DIRECTION_BOTTOM]) {

						pointFillInfo[index][FULL_BR] = true;
						pointFillInfo[index + 1][FULL_BL] = true;
						pointFillInfo[index + boardSize + 1][FULL_TL] = true;
						pointFillInfo[index + boardSize][FULL_TR] = true;
						checkPlayer = false;
						if (RedBluePlayer) {
							redFillInfo[index][FULL_BR] = true;
							redFillInfo[index + 1][FULL_BL] = true;
							redFillInfo[index + boardSize + 1][FULL_TL] = true;
							redFillInfo[index + boardSize][FULL_TR] = true;
							redFill += 1;
						}

						else {
							blueFillInfo[index][FULL_BR] = true;
							blueFillInfo[index + 1][FULL_BL] = true;
							blueFillInfo[index + boardSize + 1][FULL_TL] = true;
							blueFillInfo[index + boardSize][FULL_TR] = true;
							blueFill += 1;
						}

						setImageResource(index, Game.LINE,
								String.valueOf(getDirection(index)), true);
						setImageResource(index + 1, Game.LINE,
								String.valueOf(getDirection(index + 1)), true);
						setImageResource(
								index + boardSize + 1,
								Game.LINE,
								String.valueOf(getDirection(index + boardSize
										+ 1)), true);
						setImageResource(
								index + boardSize,
								Game.LINE,
								String.valueOf(getDirection(index + boardSize)),
								true);

						notifyDataSetChanged();

					}
				}
				if (checkPlayer)
					changePlayer();
				else
					checkOpponentPlayer();
				break;
			case DIRECTION_TOP:

				if (index % boardSize < boardSize - 1) {
					if (gridDirectionInfo[index][DIRECTION_RIGHT]
							&& gridDirectionInfo[index + 1][DIRECTION_LEFT]
							&& gridDirectionInfo[index + 1][DIRECTION_TOP]
							&& gridDirectionInfo[index - boardSize + 1][DIRECTION_BOTTOM]
							&& gridDirectionInfo[index - boardSize + 1][DIRECTION_LEFT]
							&& gridDirectionInfo[index - boardSize][DIRECTION_RIGHT]
							&& gridDirectionInfo[index - boardSize][DIRECTION_BOTTOM]
							&& gridDirectionInfo[index][DIRECTION_TOP]) {
						pointFillInfo[index][FULL_TR] = true;
						pointFillInfo[index + 1][FULL_TL] = true;
						pointFillInfo[index - boardSize + 1][FULL_BL] = true;
						pointFillInfo[index - boardSize][FULL_BR] = true;
						checkPlayer = false;
						if (RedBluePlayer) {
							redFillInfo[index][FULL_TR] = true;
							redFillInfo[index + 1][FULL_TL] = true;
							redFillInfo[index - boardSize + 1][FULL_BL] = true;
							redFillInfo[index - boardSize][FULL_BR] = true;
							redFill += 1;
						}

						else {
							blueFillInfo[index][FULL_TR] = true;
							blueFillInfo[index + 1][FULL_TL] = true;
							blueFillInfo[index - boardSize + 1][FULL_BL] = true;
							blueFillInfo[index - boardSize][FULL_BR] = true;
							blueFill += 1;
						}

						setImageResource(index, Game.LINE,
								String.valueOf(getDirection(index)), true);
						setImageResource(index + 1, Game.LINE,
								String.valueOf(getDirection(index + 1)), true);
						setImageResource(
								index - boardSize + 1,
								Game.LINE,
								String.valueOf(getDirection(index - boardSize
										+ 1)), true);
						setImageResource(
								index - boardSize,
								Game.LINE,
								String.valueOf(getDirection(index - boardSize)),
								true);

						notifyDataSetChanged();

					}
				}

				if (index % boardSize > 0) {
					if (gridDirectionInfo[index][DIRECTION_LEFT]
							&& gridDirectionInfo[index - 1][DIRECTION_RIGHT]
							&& gridDirectionInfo[index - 1][DIRECTION_TOP]
							&& gridDirectionInfo[index - boardSize - 1][DIRECTION_BOTTOM]
							&& gridDirectionInfo[index - boardSize - 1][DIRECTION_RIGHT]
							&& gridDirectionInfo[index - boardSize][DIRECTION_LEFT]
							&& gridDirectionInfo[index - boardSize][DIRECTION_BOTTOM]
							&& gridDirectionInfo[index][DIRECTION_TOP]) {
						pointFillInfo[index][FULL_TL] = true;
						pointFillInfo[index - 1][FULL_TR] = true;
						pointFillInfo[index - boardSize - 1][FULL_BR] = true;
						pointFillInfo[index - boardSize][FULL_BL] = true;
						checkPlayer = false;
						if (RedBluePlayer) {
							redFillInfo[index][FULL_TL] = true;
							redFillInfo[index - 1][FULL_TR] = true;
							redFillInfo[index - boardSize - 1][FULL_BR] = true;
							redFillInfo[index - boardSize][FULL_BL] = true;
							redFill += 1;
						}

						else {
							blueFillInfo[index][FULL_TL] = true;
							blueFillInfo[index - 1][FULL_TR] = true;
							blueFillInfo[index - boardSize - 1][FULL_BR] = true;
							blueFillInfo[index - boardSize][FULL_BL] = true;
							blueFill += 1;
						}

						setImageResource(index, Game.LINE,
								String.valueOf(getDirection(index)), true);
						setImageResource(index - 1, Game.LINE,
								String.valueOf(getDirection(index - 1)), true);
						setImageResource(
								index - boardSize - 1,
								Game.LINE,
								String.valueOf(getDirection(index - boardSize
										- 1)), true);
						setImageResource(
								index - boardSize,
								Game.LINE,
								String.valueOf(getDirection(index - boardSize)),
								true);

						notifyDataSetChanged();

					}
				}
				if (checkPlayer)
					changePlayer();
				else
					checkOpponentPlayer();
				break;
			case DIRECTION_BOTTOM:

				if (index % boardSize > 0) {
					if (gridDirectionInfo[index][DIRECTION_LEFT]
							&& gridDirectionInfo[index - 1][DIRECTION_RIGHT]
							&& gridDirectionInfo[index - 1][DIRECTION_BOTTOM]
							&& gridDirectionInfo[index + boardSize - 1][DIRECTION_TOP]
							&& gridDirectionInfo[index + boardSize - 1][DIRECTION_RIGHT]
							&& gridDirectionInfo[index + boardSize][DIRECTION_LEFT]
							&& gridDirectionInfo[index + boardSize][DIRECTION_TOP]
							&& gridDirectionInfo[index][DIRECTION_BOTTOM]) {

						pointFillInfo[index][FULL_BL] = true;
						pointFillInfo[index - 1][FULL_BR] = true;
						pointFillInfo[index + boardSize - 1][FULL_TR] = true;
						pointFillInfo[index + boardSize][FULL_TL] = true;

						checkPlayer = false;
						if (RedBluePlayer) {
							redFillInfo[index][FULL_BL] = true;
							redFillInfo[index - 1][FULL_BR] = true;
							redFillInfo[index + boardSize - 1][FULL_TR] = true;
							redFillInfo[index + boardSize][FULL_TL] = true;
							redFill += 1;

						}

						else {
							blueFillInfo[index][FULL_BL] = true;
							blueFillInfo[index - 1][FULL_BR] = true;
							blueFillInfo[index + boardSize - 1][FULL_TR] = true;
							blueFillInfo[index + boardSize][FULL_TL] = true;
							blueFill += 1;
						}

						setImageResource(index, Game.LINE,
								String.valueOf(getDirection(index)), true);
						setImageResource(index - 1, Game.LINE,
								String.valueOf(getDirection(index - 1)), true);
						setImageResource(
								index + boardSize - 1,
								Game.LINE,
								String.valueOf(getDirection(index + boardSize
										- 1)), true);
						setImageResource(
								index + boardSize,
								Game.LINE,
								String.valueOf(getDirection(index + boardSize)),
								true);

						notifyDataSetChanged();

					}
				}

				if (index % boardSize < boardSize - 1) {
					if (gridDirectionInfo[index][DIRECTION_RIGHT]
							&& gridDirectionInfo[index + 1][DIRECTION_LEFT]
							&& gridDirectionInfo[index + 1][DIRECTION_BOTTOM]
							&& gridDirectionInfo[index + boardSize + 1][DIRECTION_TOP]
							&& gridDirectionInfo[index + boardSize + 1][DIRECTION_LEFT]
							&& gridDirectionInfo[index + boardSize][DIRECTION_RIGHT]
							&& gridDirectionInfo[index + boardSize][DIRECTION_TOP]
							&& gridDirectionInfo[index][DIRECTION_BOTTOM]) {

						pointFillInfo[index][FULL_BR] = true;
						pointFillInfo[index + 1][FULL_BL] = true;
						pointFillInfo[index + boardSize + 1][FULL_TL] = true;
						pointFillInfo[index + boardSize][FULL_TR] = true;

						checkPlayer = false;
						if (RedBluePlayer) {
							redFillInfo[index][FULL_BR] = true;
							redFillInfo[index + 1][FULL_BL] = true;
							redFillInfo[index + boardSize + 1][FULL_TL] = true;
							redFillInfo[index + boardSize][FULL_TR] = true;
							redFill += 1;
						}

						else {
							blueFillInfo[index][FULL_BR] = true;
							blueFillInfo[index + 1][FULL_BL] = true;
							blueFillInfo[index + boardSize + 1][FULL_TL] = true;
							blueFillInfo[index + boardSize][FULL_TR] = true;
							blueFill += 1;
						}

						setImageResource(index, Game.LINE,
								String.valueOf(getDirection(index)), true);
						setImageResource(index + 1, Game.LINE,
								String.valueOf(getDirection(index + 1)), true);
						setImageResource(
								index + boardSize + 1,
								Game.LINE,
								String.valueOf(getDirection(index + boardSize
										+ 1)), true);
						setImageResource(
								index + boardSize,
								Game.LINE,
								String.valueOf(getDirection(index + boardSize)),
								true);

						notifyDataSetChanged();

					}
				}
				if (checkPlayer)
					changePlayer();
				else
					checkOpponentPlayer();
				break;
			}

		}

		public int FillBoxInfo(int index) {

			int direction = 0;
			Game game = mainActivity.getGame();
			// Log.i("boolean value",String.valueOf(gridDirectionInfo[index][DIRECTION_LEFT])
			// +
			// String.valueOf(gridDirectionInfo[index][DIRECTION_RIGHT])+String.valueOf(gridDirectionInfo[index][DIRECTION_TOP])+String.valueOf(gridDirectionInfo[index][DIRECTION_BOTTOM])
			// );
			if (pointFillInfo[index][FULL_TR] && pointFillInfo[index][FULL_TL]
					&& pointFillInfo[index][FULL_BL]
					&& pointFillInfo[index][FULL_BR])
				direction = FULL_TRTLBLBR;
			else if (pointFillInfo[index][FULL_TL]
					&& pointFillInfo[index][FULL_BL]
					&& pointFillInfo[index][FULL_BR])
				direction = FULL_TLBLBR;
			else if (pointFillInfo[index][FULL_TR]
					&& pointFillInfo[index][FULL_BL]
					&& pointFillInfo[index][FULL_BR])
				direction = FULL_TRBLBR;
			else if (pointFillInfo[index][FULL_TR]
					&& pointFillInfo[index][FULL_TL]
					&& pointFillInfo[index][FULL_BR])
				direction = FULL_TRTLBR;
			else if (pointFillInfo[index][FULL_TR]
					&& pointFillInfo[index][FULL_TL]
					&& pointFillInfo[index][FULL_BL])
				direction = FULL_TRTLBL;

			else if (pointFillInfo[index][FULL_BL]
					&& pointFillInfo[index][FULL_BR])
				direction = FULL_BLBR;
			else if (pointFillInfo[index][FULL_TL]
					&& pointFillInfo[index][FULL_BR])
				direction = FULL_TLBR;
			else if (pointFillInfo[index][FULL_TL]
					&& pointFillInfo[index][FULL_BL])
				direction = FULL_TLBL;
			else if (pointFillInfo[index][FULL_TR]
					&& pointFillInfo[index][FULL_BR])
				direction = FULL_TRBR;
			else if (pointFillInfo[index][FULL_TR]
					&& pointFillInfo[index][FULL_BL])
				direction = FULL_TRBL;
			else if (pointFillInfo[index][FULL_TR]
					&& pointFillInfo[index][FULL_TL])
				direction = FULL_TRTL;
			else if (pointFillInfo[index][FULL_BR])
				direction = FULL_BR;
			else if (pointFillInfo[index][FULL_BL])
				direction = FULL_BL;
			else if (pointFillInfo[index][FULL_TL])
				direction = FULL_TL;
			else if (pointFillInfo[index][FULL_TR])
				direction = FULL_TL;

			else
				direction = -1;

			return direction;
		}

		public String FillBoxInfoRedBlue(int index) {

			String value = "";
			String bluefillString = "";
			String redfillString = "";
			Game game = mainActivity.getGame();
			if (blueFillInfo[index][FULL_TR] || blueFillInfo[index][FULL_TL]
					|| blueFillInfo[index][FULL_BL]
					|| blueFillInfo[index][FULL_BR]) {
				bluefillString = "_b_";
				if (blueFillInfo[index][FULL_TR])
					bluefillString = bluefillString + "1";
				else
					bluefillString = bluefillString + "0";
				if (blueFillInfo[index][FULL_TL])
					bluefillString = bluefillString + "1";
				else
					bluefillString = bluefillString + "0";
				if (blueFillInfo[index][FULL_BL])
					bluefillString = bluefillString + "1";
				else
					bluefillString = bluefillString + "0";
				if (blueFillInfo[index][FULL_BR])
					bluefillString = bluefillString + "1";
				else
					bluefillString = bluefillString + "0";
			}

			if (redFillInfo[index][FULL_TR] || redFillInfo[index][FULL_TL]
					|| redFillInfo[index][FULL_BL]
					|| redFillInfo[index][FULL_BR]) {
				redfillString = "_r_";
				if (redFillInfo[index][FULL_TR])
					redfillString = redfillString + "1";
				else
					redfillString = redfillString + "0";
				if (redFillInfo[index][FULL_TL])
					redfillString = redfillString + "1";
				else
					redfillString = redfillString + "0";
				if (redFillInfo[index][FULL_BL])
					redfillString = redfillString + "1";
				else
					redfillString = redfillString + "0";
				if (redFillInfo[index][FULL_BR])
					redfillString = redfillString + "1";
				else
					redfillString = redfillString + "0";
			}
			value = bluefillString + redfillString;
			return value;
		}

		public void changePlayer() {
			if (RedBluePlayer) {
				RedBluePlayer = false;
				if (opponentPlayer.equals("Blue") && !playerType) {
					opponentMove();
				}

			} else {
				RedBluePlayer = true;
				if (opponentPlayer.equals("Red") && !playerType) {
					opponentMove();
				}
			}

		}

		public void checkOpponentPlayer() {
			if (RedBluePlayer) {
				if (opponentPlayer.equals("Red") && !playerType) {
					opponentMove();
				}
			} else {
				if (opponentPlayer.equals("Blue") && !playerType) {
					opponentMove();
				}
			}

		}

		public void opponentMove() {

			Random randGird = new Random();
			Random randDirection = new Random();
			int blankGrid[] = new int[boardSize * boardSize];

			final ProgressDialog ringProgressDialog = ProgressDialog.show(
					MainActivity.this, opponentPlayer + "'s Move",
					"Please Wait ...", true);
			ringProgressDialog.setCancelable(true);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {

					}
					ringProgressDialog.dismiss();
				}
			}).start();

			int opoGrid = randGird.nextInt((boardSize * boardSize));
			int opoGridDirection = randDirection.nextInt(4);
			int i = 1;
			Log.i("opponent", "grid :" + opoGrid + " Direction :"
					+ opoGridDirection);
			int j = 0, l = 0;
			for (j = 0; j < (boardSize * boardSize); j++) {
				if (j % boardSize > 0 && j + 1 > boardSize
						&& !gridDirectionInfo[j][DIRECTION_LEFT]) {

					if (gridDirectionInfo[j - boardSize - 1][DIRECTION_RIGHT]
							&& gridDirectionInfo[j - 1][DIRECTION_TOP]
							&& gridDirectionInfo[j - boardSize][DIRECTION_BOTTOM]) {

						opoGrid = j;
						opoGridDirection = DIRECTION_LEFT;
						i = 0;
						Log.i(" Loop", " grid :" + opoGrid + " Direction :"
								+ opoGridDirection);
						break;
					}

				}
				if (j % boardSize < boardSize - 1
						&& j < (boardSize * (boardSize - 1))
						&& !gridDirectionInfo[j][DIRECTION_RIGHT]) {
					if (gridDirectionInfo[j + boardSize + 1][DIRECTION_LEFT]
							&& gridDirectionInfo[j + boardSize][DIRECTION_TOP]
							&& gridDirectionInfo[j + 1][DIRECTION_BOTTOM]) {
						i = 0;
						opoGrid = j;
						opoGridDirection = DIRECTION_RIGHT;
						Log.i(" Loop", " grid :" + opoGrid + " Direction :"
								+ opoGridDirection);
						break;
					}
				}
				if (!gridDirectionInfo[j][DIRECTION_TOP] && j + 1 > boardSize
						&& j % boardSize < boardSize - 1) {
					if (gridDirectionInfo[j - boardSize][DIRECTION_RIGHT]
							&& gridDirectionInfo[j + 1][DIRECTION_LEFT]
							&& gridDirectionInfo[j - boardSize + 1][DIRECTION_BOTTOM]) {
						i = 0;
						opoGrid = j;
						opoGridDirection = DIRECTION_TOP;
						Log.i(" Loop", " grid :" + opoGrid + " Direction :"
								+ opoGridDirection);
						break;
					}
				}
				if (!gridDirectionInfo[j][DIRECTION_BOTTOM]
						&& j % boardSize > 0
						&& j < (boardSize * (boardSize - 1))) {
					if (gridDirectionInfo[j - 1][DIRECTION_RIGHT]
							&& gridDirectionInfo[j + boardSize - 1][DIRECTION_TOP]
							&& gridDirectionInfo[j + boardSize][DIRECTION_LEFT]) {
						i = 0;
						opoGrid = j;
						opoGridDirection = DIRECTION_BOTTOM;
						Log.i(" Loop", " grid :" + opoGrid + " Direction :"
								+ opoGridDirection);
						break;
					}
				}
			}
			Log.i("After loop", " grid :" + opoGrid + " Direction :"
					+ opoGridDirection);

			while (i > 0) {

				if (opoGridDirection > 1) {
					if (opoGridDirection == DIRECTION_TOP) {
						if (opoGrid + 1 <= boardSize) {
							opoGrid = randGird.nextInt((boardSize * boardSize));
							opoGridDirection = randDirection.nextInt(4);
						} else {
							if (gridDirectionInfo[opoGrid][opoGridDirection]) {
								opoGrid = randGird
										.nextInt((boardSize * boardSize));
								opoGridDirection = randDirection.nextInt(4);
							} else
								break;
						}
					}

					else {
						if (opoGrid >= (boardSize * (boardSize - 1))) {
							opoGrid = randGird.nextInt((boardSize * boardSize));
							opoGridDirection = randDirection.nextInt(4);
						} else {
							if (gridDirectionInfo[opoGrid][opoGridDirection]) {
								opoGrid = randGird
										.nextInt((boardSize * boardSize));
								opoGridDirection = randDirection.nextInt(4);
							} else
								break;
						}

					}
				} else {

					if (opoGridDirection == DIRECTION_RIGHT) {
						if (opoGrid % boardSize >= boardSize - 1) {
							opoGrid = randGird.nextInt((boardSize * boardSize));
							opoGridDirection = randDirection.nextInt(4);

						} else {
							if (gridDirectionInfo[opoGrid][opoGridDirection]) {
								opoGrid = randGird
										.nextInt((boardSize * boardSize));
								opoGridDirection = randDirection.nextInt(4);
							} else
								break;
						}

					} else {
						if (opoGrid % boardSize <= 0) {
							opoGrid = randGird.nextInt((boardSize * boardSize));
							opoGridDirection = randDirection.nextInt(4);
						} else {
							if (gridDirectionInfo[opoGrid][opoGridDirection]) {
								opoGrid = randGird
										.nextInt((boardSize * boardSize));
								opoGridDirection = randDirection.nextInt(4);
							} else
								break;
						}
					}

				}
				Log.i("Before Loop", " grid :" + opoGrid + " Direction :"
						+ opoGridDirection);

				i++;
				if (i > 10) {
					j = 0;
					l = 0;
					for (j = 0; j < (boardSize * boardSize); j++) {
						if (!gridDirectionInfo[j][DIRECTION_LEFT]
								|| !gridDirectionInfo[j][DIRECTION_RIGHT]
								|| !gridDirectionInfo[j][DIRECTION_TOP]
								|| !gridDirectionInfo[j][DIRECTION_BOTTOM]) {

							blankGrid[l] = j;
							l++;
						}
					}
					l = 0;
					boolean loop = true;
					while (loop && l < blankGrid.length) {
						opoGrid = blankGrid[l];
						l++;
						if (opoGrid % boardSize > 0
								&& !gridDirectionInfo[opoGrid][DIRECTION_LEFT]) {
							opoGridDirection = DIRECTION_LEFT;
							loop = false;
						} else if (opoGrid % boardSize < boardSize - 1
								&& !gridDirectionInfo[opoGrid][DIRECTION_RIGHT]) {
							opoGridDirection = DIRECTION_RIGHT;
							loop = false;
						} else if (opoGrid + 1 > boardSize
								&& !gridDirectionInfo[opoGrid][DIRECTION_TOP]) {
							opoGridDirection = DIRECTION_TOP;
							loop = false;
						} else if (opoGrid < (boardSize * (boardSize - 1))
								&& !gridDirectionInfo[opoGrid][DIRECTION_BOTTOM]) {
							opoGridDirection = DIRECTION_BOTTOM;
							loop = false;
						}

					}

					Log.i("after 10", "chang denst grid :" + opoGrid
							+ "changing denst Direction :" + opoGridDirection);
					break;

				}

				Log.i("changing", "chang grid :" + opoGrid
						+ "changing Direction :" + opoGridDirection);
			}


			if (opoGridDirection > 1) {
				if (opoGridDirection == 2) {
					if (opoGrid + 1 > boardSize) {
						if (!gridDirectionInfo[opoGrid][DIRECTION_TOP]) {
							gridDirectionInfo[opoGrid][DIRECTION_TOP] = true;
							updateBoard(game.setLine(opoGrid));

							gridDirectionInfo[opoGrid - boardSize][DIRECTION_BOTTOM] = true;
							updateBoard(game.setLine(opoGrid - boardSize));

							IsFillBox(opoGrid, DIRECTION_TOP);

						}
					}
				} else {
					if (opoGrid < (boardSize * (boardSize - 1))) {

						if (!gridDirectionInfo[opoGrid][DIRECTION_BOTTOM]) {
							gridDirectionInfo[opoGrid][DIRECTION_BOTTOM] = true;
							updateBoard(game.setLine(opoGrid));

							gridDirectionInfo[opoGrid + boardSize][DIRECTION_TOP] = true;
							updateBoard(game.setLine(opoGrid + boardSize));

							IsFillBox(opoGrid, DIRECTION_BOTTOM);

						}

					}

				}

				notifyDataSetChanged();

			} else {

				if (opoGridDirection == 1) {
					if (opoGrid % boardSize < boardSize - 1) {

						if (!gridDirectionInfo[opoGrid][DIRECTION_RIGHT]) {
							gridDirectionInfo[opoGrid][DIRECTION_RIGHT] = true;
							updateBoard(game.setLine(opoGrid));

							gridDirectionInfo[opoGrid + 1][DIRECTION_LEFT] = true;
							updateBoard(game.setLine(opoGrid + 1));

							IsFillBox(opoGrid, DIRECTION_RIGHT);

						}
					}

				} else {
					if (opoGrid % boardSize > 0) {
						if (!gridDirectionInfo[opoGrid][DIRECTION_LEFT]) {
							gridDirectionInfo[opoGrid][DIRECTION_LEFT] = true;
							updateBoard(game.setLine(opoGrid));

							gridDirectionInfo[opoGrid - 1][DIRECTION_RIGHT] = true;
							updateBoard(game.setLine(opoGrid - 1));
							IsFillBox(opoGrid, DIRECTION_LEFT);

						}
					}
				}

				notifyDataSetChanged();

			}
		}
	}
}