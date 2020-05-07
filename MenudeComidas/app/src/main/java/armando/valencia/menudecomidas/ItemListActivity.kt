package armando.valencia.menudecomidas

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import armando.valencia.menudecomidas.dummy.DummyContent
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list.*
import kotlinx.android.synthetic.main.item_list_content.view.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ItemListActivity : AppCompatActivity() {

    companion object{
        private val PATH_FOOD = "food"
        private val PATH_CODE = "code"
        private val PATH_PROFILE = "profile"
    }

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        if (item_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        btnSave.setOnClickListener {
            var comida:DummyContent.Comida = DummyContent.Comida(etName.text.toString().trim(), etPrice.text.toString().trim())

            var database:FirebaseDatabase = FirebaseDatabase.getInstance()
            var reference:DatabaseReference = database.getReference(PATH_FOOD)

            var comidaUpdate: DummyContent.Comida? = DummyContent.getComida(comida.getName())
            if (comidaUpdate != null){
                reference.child(comidaUpdate.getId()).setValue(comida)
            }else{
                reference.push().setValue(comida)
            }

            etName.setText("")
            etPrice.setText("")
        }

        setupRecyclerView(item_list)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, twoPane)

        var database:FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference:DatabaseReference = database.getReference(PATH_FOOD)

        reference.addChildEventListener(object:ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(applicationContext, "Cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                Toast.makeText(applicationContext, "Moved", Toast.LENGTH_SHORT).show()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                var comida: DummyContent.Comida? = p0.getValue(DummyContent.Comida::class.java)
                comida?.setId(p0.key)

                if (DummyContent.ITEMS.contains(comida)) {
                    DummyContent.updateItem(comida!!)
                }
                (recyclerView.adapter as SimpleItemRecyclerViewAdapter).notifyDataSetChanged()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var comida: DummyContent.Comida? = p0.getValue(DummyContent.Comida::class.java)
                comida?.setId(p0.key)

                if (!DummyContent.ITEMS.contains(comida)) {
                    DummyContent.addItem(comida!!)
                }
                (recyclerView.adapter as SimpleItemRecyclerViewAdapter).notifyDataSetChanged()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                var comida: DummyContent.Comida? = p0.getValue(DummyContent.Comida::class.java)
                comida?.setId(p0.key)

                if (DummyContent.ITEMS.contains(comida)) {
                    DummyContent.deleteItem(comida!!)
                }
                (recyclerView.adapter as SimpleItemRecyclerViewAdapter).notifyDataSetChanged()
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_info -> {
//                Toast.makeText(this, "Funciona!", Toast.LENGTH_SHORT).show()
                var code:String? = ""
                val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                val reference: DatabaseReference = database.getReference(PATH_PROFILE).child(PATH_CODE)

                reference.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        Toast.makeText(applicationContext, "No se puede cargar el código.", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        code = p0.getValue(String::class.java)
                        Toast.makeText(applicationContext, code, Toast.LENGTH_SHORT).show()
                    }
                })

            }
        }
        return super.onOptionsItemSelected(item)
    }

    class SimpleItemRecyclerViewAdapter(private val parentActivity: ItemListActivity,
                                        private val values: List<DummyContent.Comida>,
                                        private val twoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as DummyContent.Comida
                if (twoPane) {
                    val fragment = ItemDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(ItemDetailFragment.ARG_ITEM_ID, item.getId())
                        }
                    }
                    parentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit()
                } else {
                    val intent = Intent(v.context, ItemDetailActivity::class.java).apply {
                        putExtra(ItemDetailFragment.ARG_ITEM_ID, item.getId())
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = "$"+item.getPrice()
            holder.contentView.text = item.getName()
            holder.btnDelete.setOnClickListener { 
                var database:FirebaseDatabase = FirebaseDatabase.getInstance()
                var reference:DatabaseReference = database.getReference(PATH_FOOD)
                reference.child(item.getId()).removeValue()
            }

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.id_text
            val contentView: TextView = view.content
            val btnDelete: Button = view.btnDelete
        }
    }
}
