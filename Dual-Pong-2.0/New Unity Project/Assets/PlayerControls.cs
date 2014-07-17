using UnityEngine;
using System.Collections;

public class PlayerControls : MonoBehaviour {

	public KeyCode moveUp, moveDown;
	public float speed = 10;

	// Use this for initialization
	void Start () {

	}	
	// Update is called once per frame
	void Update () {
				if (Input.GetKey (moveUp)) { 
						MoveUp();
				} else if (Input.GetKey (moveDown)) { 
						MoveDown();
				}
	}

	void MoveUp()
	{
		rigidbody2D.velocity = new Vector2 (0, speed);	
	}

	void MoveDown()
	{
		rigidbody2D.velocity = new Vector2 (0, -speed);		
	}
	
	void PlayerStop()
	{
		rigidbody2D.velocity = new Vector2 (0, 0);
	}
}

