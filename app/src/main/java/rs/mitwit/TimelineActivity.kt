package rs.mitwit

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_timeline.*
import kotlinx.android.synthetic.main.item_user_post.view.*
import org.jetbrains.anko.*
import rs.mitwit.di.Injector
import rs.mitwit.models.Post
import rs.mitwit.models.Timeline
import rs.mitwit.posts.TimelineView
import java.text.DateFormat
import java.util.*

class TimelineActivity : AppCompatActivity(), TimelineView {

    private lateinit var postsAdapter: PostsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager


    val presenter by lazy {
        Injector.provideTimelinePresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        viewManager = LinearLayoutManager(this)
        postsAdapter = PostsAdapter(listOf())

        posts.apply {
            layoutManager = viewManager
            adapter = postsAdapter
        }

        presenter.onCreate()
    }

    inner class PostsAdapter(private var items: List<Post>) :
        RecyclerView.Adapter<PostsAdapter.MyViewHolder>() {

        fun updateList(data: List<Post>) {
            items = data
            notifyDataSetChanged()
        }

        inner class MyViewHolder(val viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PostsAdapter.MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_post, parent, false) as ViewGroup
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val post = items[position]
            holder.viewGroup.title_textView.text = post.title
            holder.viewGroup.comment_textView.text = post.content
            holder.viewGroup.date_textView.text = formatTime(post.time.epochTime)
            holder.viewGroup.delete_imageView.setOnClickListener {
                presenter.onPostDeleteClicked(post)
            }
        }

        private fun formatTime(epochTime: Long): CharSequence? {
            return DateFormat.getDateTimeInstance().format(Date(epochTime))
        }

        override fun getItemCount() = items.size
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_timeline, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                presenter.onRefreshClicked()
                true
            }
            R.id.logout_item -> {
                presenter.logoutClicked()
                true
            }
            R.id.add_post -> {
                presenter.onAddPostClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun gotoLoginScreen() {
        startActivity(Intent(this, LoadingActivity::class.java))
    }

    override fun setData(timeline: Timeline) {
        postsAdapter.updateList(timeline.posts)
    }

    @Suppress("DEPRECATION")
    var progressDialog: ProgressDialog? = null

    override fun setLoading() {
        @Suppress("DEPRECATION")
        progressDialog = indeterminateProgressDialog(getString(R.string.loading))
    }

    override fun clearLoading() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    override fun notifyDeleteFailed() {
        Toast.makeText(this, getString(R.string.deleting_failed), Toast.LENGTH_SHORT).show()
    }

    var dialog: DialogInterface? = null

    override fun openCreatePostUi() {
        dialog = alert {
            title = getString(R.string.create_post)
            isCancelable = true
            customView {
                linearLayout {
                    orientation = LinearLayout.VERTICAL
                    textView(getString(R.string.post_title))
                    val title = editText("")
                    textView(getString(R.string.content))
                    val content = editText("")
                    button(getString(R.string.create)) {
                        setOnClickListener {
                            presenter.onCreatePostClicked(title.text.toString(), content.text.toString())
                        }
                    }
                    padding = dip(16)
                }
            }
            onCancelled {
                dialog = null
            }
        }.show()
    }

    override fun closeCreatePostUi() {
        dialog?.apply { dismiss() }
    }

    override fun notifyPostingFailed() {
        Toast.makeText(this, getString(R.string.posting_failed), Toast.LENGTH_SHORT).show()
    }

}
