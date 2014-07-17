using UnityEngine;
using System.Collections;

public class SideWalls : MonoBehaviour {

	// Update is called once per frame
	void OnTriggerEnter2D (Collider2D collInfo) {
		if (collInfo.name == "Ball") {
			string wallName = transform.name;
			GameManager.Score (wallName);
			collInfo.gameObject.SendMessage("ResetBall");
		}		
	
	}
}
