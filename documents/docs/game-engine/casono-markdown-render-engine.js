const marked = {
    parse: function (md) {
        if (!md) return "";

        let toc = [];

        md = md.replace(/^(#{1,3})\s+(.*)$/gim, (m, hashes, title) => {
            const level = hashes.length;

            const id = title
                .toLowerCase()
                .trim()
                .replace(/[^\wäöüß]+/g, "-")
                .replace(/-+/g, "-")
                .replace(/^-|-$/g, "");

            toc.push({ level, title, id });

            return `
                <h${level} id="${id}" class="cm-h${level}">
                    ${title}
                </h${level}>
            `;
        });

        md = md.replace(/```([\s\S]*?)```/g, (_, code) => {
            return `<pre class="cm-code"><code>${escapeHtml(code)}</code></pre>`;
        });

        md = md.replace(/!\[(.*?)\]\((.*?)\)/gim,
            `<img alt="$1" src="$2" class="cm-img">`
        );

        md = md.replace(/\[(.*?)\]\((.*?)\)/gim,
            `<a href="$2" target="_blank" class="cm-link">$1</a>`
        );

        md = md.replace(/`(.*?)`/gim,
            `<code class="cm-inline">$1</code>`
        );

        md = md.replace(/\*\*(.*?)\*\*/gim, "<b>$1</b>");
        md = md.replace(/\*(.*?)\*/gim, "<i>$1</i>");

        md = md.replace(/^\s*-\s(.+)$/gim, "<li>$1</li>");
        md = wrapLists(md);

        md = md
            .replace(/\n{2,}/g, "</p><p>")
            .replace(/^(?!<h|<ul|<pre|<li|<\/)(.+)$/gim, "<p>$1</p>");

        return buildToc(toc) + md;
    }
};

function buildToc(items) {
    if (!items.length) return "";

    let html = `<div class="cm-toc">`;

    items.forEach(i => {
        html += `
            <div class="cm-toc-item level-${i.level}">
                <a href="#${i.id}">${i.title}</a>
            </div>
        `;
    });

    html += `</div>`;
    return html;
}

function wrapLists(html) {
    return html.replace(/(<li>.*?<\/li>)/gs, "<ul>$1</ul>");
}

function escapeHtml(text) {
    return text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
}

window.marked = marked;
