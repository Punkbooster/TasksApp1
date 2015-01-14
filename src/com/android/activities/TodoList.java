package com.android.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.android.object.TodoDocument;

import ru.javabegin.training.adnroid.todoproject.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TodoList extends Activity {

	public static String TODO_DOCUMENT = "com.android.TodoDocument";
	public static int TODO_DETAILS_REQUEST = 1;    // sminna dla togo schob znatu kolu powertajetsya docherne activity

	private ListView listTasks;

	private ArrayAdapter<TodoDocument> arrayAdapter;
	
	private static ArrayList<TodoDocument> listDocuments = new ArrayList<TodoDocument>();

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list);

		listTasks = (ListView) findViewById(R.id.listTasks);

		listTasks.setOnItemClickListener(new ListViewClickListener());
		
		listTasks.setEmptyView(findViewById(R.id.emptyView));

		getActionBar().setDisplayHomeAsUpEnabled(false);

		fillTodoList();

	}

	private void fillTodoList() {
		// wkazyjemo shablon dla koznoji liniji list view arraj adapter przechowuje listDocument
		// arrayAdapter przyjmuje dane i wstawia w listview
		arrayAdapter = new ArrayAdapter<TodoDocument>(this, R.layout.listview_row, listDocuments);
		listTasks.setAdapter(arrayAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_task: {
			TodoDocument todoDocument = new TodoDocument();
			todoDocument.setName(getResources()
					.getString(R.string.new_document));
			showDocument(todoDocument);                         // wuklukajemo intent
			return true;
		}

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// peredajemo dani w dryge activity 
	private void showDocument(TodoDocument todoDocument) {
		Intent intentTodoDetails = new Intent(this, TodoDetails.class);
		intentTodoDetails.putExtra(TODO_DOCUMENT, todoDocument);
		startActivityForResult(intentTodoDetails, TODO_DETAILS_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { // wuklukajetsya kolu zakruwajetsya
		                                                                            // ocherne activity
		if (requestCode == TODO_DETAILS_REQUEST) {

			TodoDocument todoDocument = null;
			switch (resultCode) {
			case RESULT_CANCELED:                      // standartnuj identufikator
				Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
				break;

			case TodoDetails.RESULT_SAVE:               // stworenuj identufikator w klasi todoDetails         
				todoDocument = (TodoDocument) data
						.getSerializableExtra(TODO_DOCUMENT);  // wyznaczamy todoDocument z naszego intent za pomocy get
				todoDocument.setCreateDate(new Date());        // serializable extra
				addDocument(todoDocument);
				Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
				break;

			case TodoDetails.RESULT_DELETE:
				todoDocument = (TodoDocument) data              // w data przechowuje sie intent ktory wrocil z powrotem
						.getSerializableExtra(TODO_DOCUMENT);
				deleteDocument((TodoDocument) data
						.getSerializableExtra(TODO_DOCUMENT));
				break;

			default:
				break;
			}
		}
	}

	
	@SuppressLint("NewApi")
	private void addDocument(TodoDocument todoDocument) {
		
		if (todoDocument.getNumber()==null){  //  jkascho dokyment nowuj (tilku stworyjetsya)
			listDocuments.add(todoDocument);
		}else{                                // jakscho dokyment yze stworenuj ranishe
			listDocuments.set(todoDocument.getNumber(), todoDocument);
		}
	
		Collections.sort(listDocuments);       // widpowidaje za sortywannya po dati modufikaciji
		arrayAdapter.notifyDataSetChanged();

	}

	@SuppressLint("NewApi")
	private void deleteDocument(TodoDocument todoDocument) {
		listDocuments.remove(todoDocument.getNumber().intValue());
		arrayAdapter.notifyDataSetChanged();                       // sygnalizujemy adapteru ze dokument zostal zmieniony
	}

	class ListViewClickListener implements OnItemClickListener {

		// pozycjonowanie zadan
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			TodoDocument todoDocument = (TodoDocument) parent.getAdapter().getItem(position);
			todoDocument.setNumber(position);
			showDocument(todoDocument);
		}

	}

}
