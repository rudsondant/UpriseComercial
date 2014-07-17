using UnityEngine;
using System.Collections;

public class GameManager : MonoBehaviour {

	public static bool pleyer1Connected = false, pleyer2Connected = false;
	static int player01Score = 0;
	static int player02Score = 0;

	public GUISkin template;

	// Use this for initialization
	void Start () {
	
	}

	public static void Score (string wallName) {
		if (wallName == "rightWall") {
					player01Score += 1;
				} else {
					player02Score += 1;
				}
	}

	void OnGUI()
	{
		GUI.skin = template;
		GUI.Label (new Rect(Screen.width/2 - 150, 20, 100, 100), "" + player01Score);
		GUI.Label (new Rect(Screen.width/2 + 150, 20, 100, 100), "" + player02Score);
	}
}
